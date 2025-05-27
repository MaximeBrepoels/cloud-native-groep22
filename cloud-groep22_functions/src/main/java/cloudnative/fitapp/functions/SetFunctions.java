package cloudnative.fitapp.functions;

import cloudnative.fitapp.security.SimplePasswordEncoder;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.service.SetService;
import cloudnative.fitapp.service.ExerciseService;
import cloudnative.fitapp.service.WorkoutService;
import cloudnative.fitapp.service.UserService;
import cloudnative.fitapp.security.SimplePasswordEncoder;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions for set management endpoints.
 */
public class SetFunctions extends BaseFunctionHandler {

    /**
     * Get all sets - GET /api/sets
     */
    @FunctionName("GetAllSets")
    public HttpResponseMessage getAllSets(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "sets",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all sets");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Create services
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);
            SetService setService = new SetService(cosmosDBService, exerciseService);

            List<Set> sets = setService.getAllSets();
            return createResponse(request, sets);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get set by ID - GET /api/sets/{id}
     */
    @FunctionName("GetSetById")
    public HttpResponseMessage getSetById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "sets/byId/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting set by ID: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Create services
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);
            SetService setService = new SetService(cosmosDBService, exerciseService);

            Set set = setService.getSetById(Long.parseLong(id));

            if (set == null) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Set not found");
            }

            return createResponse(request, set);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Add set to exercise - POST /api/sets/exercise/{exerciseId}/addSet
     */
    @FunctionName("AddSetToExercise")
    public HttpResponseMessage addSetToExercise(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "sets/exercise/{exerciseId}/addSet",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("exerciseId") String exerciseId,
            final ExecutionContext context) {

        context.getLogger().info("Adding set to exercise: " + exerciseId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            Set set = parseBody(request, Set.class);

            // Create services
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);
            SetService setService = new SetService(cosmosDBService, exerciseService);

            Set newSet = setService.addSetToExercise(Long.parseLong(exerciseId), set);
            return createResponse(request, newSet);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update set - PUT /api/sets/{id}
     */
    @FunctionName("UpdateSet")
    public HttpResponseMessage updateSet(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "sets/update/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Updating set: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            Set set = parseBody(request, Set.class);

            // Create services
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);
            SetService setService = new SetService(cosmosDBService, exerciseService);

            Set updatedSet = setService.updateSet(Long.parseLong(id), set);
            return createResponse(request, updatedSet);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Delete set - DELETE /api/sets/{id}
     */
    @FunctionName("DeleteSet")
    public HttpResponseMessage deleteSet(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE, HttpMethod.OPTIONS},
                    route = "sets/delete/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Deleting set: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Create services
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            WorkoutService workoutService = new WorkoutService(cosmosDBService, userService);
            ExerciseService exerciseService = new ExerciseService(cosmosDBService, workoutService);
            SetService setService = new SetService(cosmosDBService, exerciseService);

            if (setService.getSetById(Long.parseLong(id)) == null) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Set not found");
            }

            setService.deleteSet(Long.parseLong(id));
            return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}