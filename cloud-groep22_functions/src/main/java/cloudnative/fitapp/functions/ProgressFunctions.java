package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Progress;
import cloudnative.fitapp.service.ProgressService;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions for progress tracking endpoints.
 */
public class ProgressFunctions extends BaseFunctionHandler {

    /**
     * Get all progress - GET /api/progress
     */
    @FunctionName("GetAllProgress")
    public HttpResponseMessage getAllProgress(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "progress",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all progress entries");

        try {
            validateToken(request);
            ProgressService progressService = getBean(ProgressService.class);
            List<Progress> progressList = progressService.getAllProgress();
            return createResponse(request, progressList);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get progress by exercise ID - GET /api/progress/{id}
     */
    @FunctionName("GetProgressByExerciseId")
    public HttpResponseMessage getProgressByExerciseId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "progress/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting progress for exercise: " + id);

        try {
            validateToken(request);
            ProgressService progressService = getBean(ProgressService.class);
            List<Progress> progressList = progressService.getProgressByExerciseId(Long.parseLong(id));
            return createResponse(request, progressList);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}