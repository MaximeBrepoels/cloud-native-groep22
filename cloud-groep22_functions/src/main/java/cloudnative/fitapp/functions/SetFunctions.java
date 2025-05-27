package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Set;
import cloudnative.fitapp.service.SetService;
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
                    methods = {HttpMethod.GET},
                    route = "sets",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all sets");

        try {
            validateToken(request);
            SetService setService = getBean(SetService.class);
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
                    methods = {HttpMethod.GET},
                    route = "sets/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting set by ID: " + id);

        try {
            validateToken(request);
            SetService setService = getBean(SetService.class);
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
                    methods = {HttpMethod.POST},
                    route = "sets/exercise/{exerciseId}/addSet",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("exerciseId") String exerciseId,
            final ExecutionContext context) {

        context.getLogger().info("Adding set to exercise: " + exerciseId);

        try {
            validateToken(request);
            Set set = parseBody(request, Set.class);
            SetService setService = getBean(SetService.class);
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
                    methods = {HttpMethod.PUT},
                    route = "sets/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Updating set: " + id);

        try {
            validateToken(request);
            Set set = parseBody(request, Set.class);
            SetService setService = getBean(SetService.class);
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
                    methods = {HttpMethod.DELETE},
                    route = "sets/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Deleting set: " + id);

        try {
            validateToken(request);
            SetService setService = getBean(SetService.class);

            if (setService.getSetById(Long.parseLong(id)) == null) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "Set not found");
            }

            setService.deleteSet(Long.parseLong(id));
            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}