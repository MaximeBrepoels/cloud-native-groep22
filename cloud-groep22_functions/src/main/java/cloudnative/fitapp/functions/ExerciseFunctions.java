package cloudnative.fitapp.functions;

import cloudnative.fitapp.cache.RedisCache;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.security.SimplePasswordEncoder;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.service.ExerciseService;
import cloudnative.fitapp.service.WorkoutService;
import cloudnative.fitapp.service.UserService;
import java.util.List;
import java.util.Optional;


public class ExerciseFunctions extends BaseFunctionHandler {


    // Get all exercises - GET /api/exercises/all
    @FunctionName("GetAllExercises")
    public HttpResponseMessage getAllExercises(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "exercises/all",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all exercises");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            List<Exercise> exercises = exerciseService.getAllExercises();
            return createResponse(request, exercises);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Create exercise - POST /api/exercises/create?workoutId={workoutId}
    @FunctionName("CreateExercise")
    public HttpResponseMessage createExercise(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "exercises/create",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Creating new exercise");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            String workoutId = getQueryParam(request, "workoutId")
                    .orElseThrow(() -> new IllegalArgumentException("workoutId parameter is required"));

            Exercise exercise = parseBody(request, Exercise.class);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            Exercise createdExercise;
            if (exercise.getName() != null && exercise.getType() == null) {
                createdExercise = exerciseService.createExerciseByName(exercise.getName());
            } else {
                createdExercise = exerciseService.createExercise(exercise, Long.parseLong(workoutId));
            }

            return createResponse(request, createdExercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Get exercise by ID - GET /api/exercises/byId/{id}
    @FunctionName("GetExerciseById")
    public HttpResponseMessage getExerciseById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "exercises/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting exercise by ID: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            Exercise exercise = exerciseService.getExerciseById(Long.parseLong(id));
            return createResponse(request, exercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Update exercise - PUT /api/exercises/update/{id}
    @FunctionName("UpdateExercise")
    public HttpResponseMessage updateExercise(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "exercises/update/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Updating exercise (simple route): " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Add debug logging
            String requestBody = request.getBody();
            context.getLogger().info("Request body: " + requestBody);

            Exercise exercise = parseBody(request, Exercise.class);

            // Log the parsed exercise
            context.getLogger().info("Parsed exercise name: " + exercise.getName());
            context.getLogger().info("Parsed exercise sets count: " + (exercise.getSets() != null ? exercise.getSets().size() : "null"));
            if (exercise.getSets() != null) {
                for (int i = 0; i < exercise.getSets().size(); i++) {
                    Set set = exercise.getSets().get(i);
                    context.getLogger().info("Set " + i + ": reps=" + set.getReps() + ", weight=" + set.getWeight() + ", duration=" + set.getDuration());
                }
            }

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            Exercise updatedExercise = exerciseService.updateExercise(Long.parseLong(id), exercise);

            // Log the result
            context.getLogger().info("Updated exercise sets count: " + (updatedExercise.getSets() != null ? updatedExercise.getSets().size() : "null"));

            return createResponse(request, updatedExercise);
        } catch (Exception e) {
            context.getLogger().severe("Error updating exercise: " + e.getMessage());
            e.printStackTrace();
            return handleException(request, e);
        }
    }


    // Get exercises by workout ID - GET /api/exercises/workout/{workoutId}
    @FunctionName("GetExercisesByWorkoutId")
    public HttpResponseMessage getExercisesByWorkoutId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "exercises/workout/{workoutId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("workoutId") String workoutId,
            final ExecutionContext context) {

        context.getLogger().info("Getting exercises for workout: " + workoutId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            RedisCache cache = RedisCache.getInstance();
            Object cachedExercises = cache.getCachedWorkoutExercises(workoutId);

            if (cachedExercises != null) {
                context.getLogger().info("Data retrieved from cache for workout exercises: " + workoutId);
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(cachedExercises)
                        .header("Content-Type", "application/json")
                        .header("FITAPP-LOCATION", "cache")
                        .build();
            }

            context.getLogger().info("Data not in cache for workout exercises: " + workoutId);
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            List<Exercise> exercises = exerciseService.getExercisesByWorkoutId(Long.parseLong(workoutId));

            cache.cacheWorkoutExercises(workoutId, exercises);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(exercises)
                    .header("Content-Type", "application/json")
                    .header("FITAPP-LOCATION", "db")
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error getting exercises by workout ID: " + e.getMessage());
            return handleException(request, e);
        }
    }


    // Delete exercise from workout - DELETE /api/exercises/workout/{workoutId}/exercise/{exerciseId}
    @FunctionName("DeleteExerciseFromWorkout")
    public HttpResponseMessage deleteExerciseFromWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE, HttpMethod.OPTIONS},
                    route = "exercises/workout/{workoutId}/exercise/{exerciseId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("workoutId") String workoutId,
            @BindingName("exerciseId") String exerciseId,
            final ExecutionContext context) {

        context.getLogger().info("Deleting exercise: " + exerciseId + " from workout: " + workoutId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            String response = exerciseService.deleteExerciseFromWorkout(
                    Long.parseLong(workoutId),
                    Long.parseLong(exerciseId)
            );
            return createResponse(request, response);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Auto increase exercise - PUT /api/exercises/increase/{id}
    @FunctionName("AutoIncreaseExercise")
    public HttpResponseMessage autoIncrease(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "exercises/increase/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Auto increasing exercise: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            Exercise exercise = exerciseService.autoIncrease(Long.parseLong(id));
            return createResponse(request, exercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Auto decrease exercise - PUT /api/exercises/decrease/{id}
    @FunctionName("AutoDecreaseExercise")
    public HttpResponseMessage autoDecrease(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "exercises/decrease/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Auto decreasing exercise: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            Exercise exercise = exerciseService.autoDecrease(Long.parseLong(id));
            return createResponse(request, exercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Get exercises by user ID - GET /api/exercises/user/{userId}
    @FunctionName("GetExercisesByUserId")
    public HttpResponseMessage getExercisesByUserId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "exercises/user/{userId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Getting exercises for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);

            List<Exercise> exercises = exerciseService.getExercisesByUserId(Long.parseLong(userId));
            return createResponse(request, exercises);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}