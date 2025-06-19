package cloudnative.fitapp.functions;

import cloudnative.fitapp.cache.RedisCache;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;

import java.util.List;
import java.util.Optional;


public class WorkoutFunctions extends BaseFunctionHandler {

    // Create workout - POST /api/workouts/create?userId={userId}
    @FunctionName("CreateWorkout")
    public HttpResponseMessage createWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "workouts",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Creating new workout");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            String userId = getQueryParam(request, "userId")
                    .orElseThrow(() -> new IllegalArgumentException("userId parameter is required"));

            Workout workoutIn = parseBody(request, Workout.class);

            // Find user to validate
            String userQuery = String.format("SELECT * FROM c WHERE c.id = '%s'", userId);
            List<User> users = cosmosDBService.query("users", userQuery, User.class);

            if (users.isEmpty()) {
                throw new IllegalArgumentException("User not found with id: " + userId);
            }

            User user = users.get(0);

            // Create workout
            Workout workout = new Workout(workoutIn.getName());
            workout.setId(String.valueOf(System.currentTimeMillis()));
            workout.setUserId(userId);

            // Save workout
            Workout savedWorkout = cosmosDBService.save("workouts", workout, userId, Workout.class);

            // Update user's workout IDs
            if (user.getWorkoutIds() == null) {
                user.setWorkoutIds(new java.util.ArrayList<>());
            }
            user.getWorkoutIds().add(workout.getId());
            cosmosDBService.update("users", user, user.getEmail(), User.class);

            return createResponse(request, savedWorkout);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

     // Get all workouts - GET /api/workouts/all
    @FunctionName("GetAllWorkouts")
    public HttpResponseMessage getAllWorkouts(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "workouts/all",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all workouts");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            List<Workout> workouts = cosmosDBService.findAll("workouts", Workout.class);
            return createResponse(request, workouts);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Get workout by ID - GET /api/workouts/{id}
    @FunctionName("GetWorkoutById")
    public HttpResponseMessage getWorkoutById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "workouts/by/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting workout by ID: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
            List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);

            if (workouts.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Workout not found");
            }

            return createResponse(request, workouts.get(0));
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Get workouts by user ID - GET /api/workouts/user/{userId}
    @FunctionName("GetWorkoutsByUserId")
    public HttpResponseMessage getWorkoutsByUserId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "workouts/user/{userId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Getting workouts for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            RedisCache cache = RedisCache.getInstance();
            Object cachedWorkouts = cache.getCachedUserWorkouts(userId);

            if (cachedWorkouts != null) {
                context.getLogger().info("Data retrieved from cache for user workouts: " + userId);
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(cachedWorkouts)
                        .header("Content-Type", "application/json")
                        .header("FITAPP-LOCATION", "cache")
                        .build();
            }

            context.getLogger().info("Data not in cache for user workouts: " + userId);
            String query = String.format("SELECT * FROM c WHERE c.userId = '%s'", userId);
            List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);

            cache.cacheUserWorkouts(userId, workouts);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(workouts)
                    .header("Content-Type", "application/json")
                    .header("FITAPP-LOCATION", "db")
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error getting user workouts: " + e.getMessage());
            return handleException(request, e);
        }
    }


    // Delete workout - DELETE /api/workouts/delete/{id}
    @FunctionName("DeleteWorkout")
    public HttpResponseMessage deleteWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE, HttpMethod.OPTIONS},
                    route = "workouts/delete/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Deleting workout: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Find workout first to get userId
            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
            List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);

            if (workouts.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Workout not found");
            }

            Workout workout = workouts.get(0);

            // Remove from user's workout list
            String userQuery = String.format("SELECT * FROM c WHERE c.id = '%s'", workout.getUserId());
            List<User> users = cosmosDBService.query("users", userQuery, User.class);

            if (!users.isEmpty()) {
                User user = users.get(0);
                if (user.getWorkoutIds() != null) {
                    user.getWorkoutIds().remove(workout.getId());
                    cosmosDBService.update("users", user, user.getEmail(), User.class);
                }
            }

            // Delete workout
            cosmosDBService.deleteById("workouts", id, workout.getUserId());

            return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Add exercise to a workout - POST /api/workouts/{id}/addExercise/{goal}
    @FunctionName("AddExerciseToWorkout")
    public HttpResponseMessage addExerciseToWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "workouts/{id}/addExercise/{goal}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            @BindingName("goal") String goal,
            final ExecutionContext context) {

        context.getLogger().info("Adding exercise to workout: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            Exercise exercise = parseBody(request, Exercise.class);

            // Find workout
            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
            List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);

            if (workouts.isEmpty()) {
                throw new IllegalArgumentException("Workout not found with id: " + id);
            }

            Workout workout = workouts.get(0);

            // Create exercise with goal settings
            Exercise newExercise = new Exercise(exercise.getName(), exercise.getType(), goal);
            newExercise = workout.addExercise(newExercise);

            // Update workout
            cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);

            return createResponse(request, newExercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Update workout - PUT /api/workouts/{id}
    @FunctionName("UpdateWorkout")
    public HttpResponseMessage updateWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "workouts/update/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Updating workout: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(request.getBody());

            String name = jsonNode.get("name").asText();
            int rest = jsonNode.get("rest").asInt();

            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
            List<Workout> workouts = cosmosDBService.query("workouts", query, Workout.class);

            if (workouts.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Workout not found");
            }

            Workout workout = workouts.get(0);
            workout.setName(name);
            workout.setRest(rest);

            Workout updatedWorkout = cosmosDBService.update("workouts", workout, workout.getUserId(), Workout.class);

            return createResponse(request, updatedWorkout);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}