package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Exercise;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.service.WorkoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Azure Functions for workout management endpoints.
 */
public class WorkoutFunctions extends BaseFunctionHandler {

    /**
     * Create workout - POST /api/workouts?userId={userId}
     */
    @FunctionName("CreateWorkout")
    public HttpResponseMessage createWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    route = "workouts",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Creating new workout");

        try {
            validateToken(request);
            String userId = getQueryParam(request, "userId")
                    .orElseThrow(() -> new IllegalArgumentException("userId parameter is required"));

            Workout workoutIn = parseBody(request, Workout.class);
            WorkoutService workoutService = getBean(WorkoutService.class);
            Workout workout = workoutService.createWorkout(workoutIn.getName(), userId);

            return createResponse(request, workout);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get all workouts - GET /api/workouts
     */
    @FunctionName("GetAllWorkouts")
    public HttpResponseMessage getAllWorkouts(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "workouts",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all workouts");

        try {
            validateToken(request);
            WorkoutService workoutService = getBean(WorkoutService.class);
            List<Workout> workouts = workoutService.getAllWorkouts();
            return createResponse(request, workouts);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get workout by ID - GET /api/workouts/{id}
     */
    @FunctionName("GetWorkoutById")
    public HttpResponseMessage getWorkoutById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "workouts/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting workout by ID: " + id);

        try {
            validateToken(request);
            WorkoutService workoutService = getBean(WorkoutService.class);
            Optional<Workout> workout = workoutService.getWorkoutById(id);

            if (workout.isPresent()) {
                return createResponse(request, workout.get());
            } else {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Workout not found");
            }
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get workouts by user ID - GET /api/workouts/user/{userId}
     */
    @FunctionName("GetWorkoutsByUserId")
    public HttpResponseMessage getWorkoutsByUserId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "workouts/user/{userId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Getting workouts for user: " + userId);

        try {
            validateToken(request);
            WorkoutService workoutService = getBean(WorkoutService.class);
            List<Workout> workouts = workoutService.getWorkoutsByUserId(userId);
            return createResponse(request, workouts);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Delete workout - DELETE /api/workouts/{id}
     */
    @FunctionName("DeleteWorkout")
    public HttpResponseMessage deleteWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    route = "workouts/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Deleting workout: " + id);

        try {
            validateToken(request);
            WorkoutService workoutService = getBean(WorkoutService.class);
            workoutService.deleteWorkout(id);
            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update workout - PUT /api/workouts/{id}
     */
    @FunctionName("UpdateWorkout")
    public HttpResponseMessage updateWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    route = "workouts/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Updating workout: " + id);

        try {
            validateToken(request);
            ObjectMapper mapper = getBean(ObjectMapper.class);
            JsonNode json = mapper.readTree(request.getBody());

            String name = json.get("name").asText();
            Integer rest = json.get("rest").asInt();
            List<String> exerciseIds = mapper.convertValue(
                            json.get("exerciseIds"),
                            mapper.getTypeFactory().constructCollectionType(List.class, Long.class)
                    ).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

            WorkoutService workoutService = getBean(WorkoutService.class);
            Workout workout = workoutService.updateWorkout(id, name, rest, exerciseIds);

            return createResponse(request, workout);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Add exercise to workout - POST /api/workouts/{id}/addExercise/{goal}
     */
    @FunctionName("AddExerciseToWorkout")
    public HttpResponseMessage addExerciseToWorkout(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    route = "workouts/{id}/addExercise/{goal}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            @BindingName("goal") String goal,
            final ExecutionContext context) {

        context.getLogger().info("Adding exercise to workout: " + id);

        try {
            validateToken(request);
            Exercise exercise = parseBody(request, Exercise.class);
            WorkoutService workoutService = getBean(WorkoutService.class);
            Exercise newExercise = workoutService.addExerciseToWorkout(id, exercise, goal);

            return createResponse(request, newExercise);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}