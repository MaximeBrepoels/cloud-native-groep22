package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.Bodyweight;
import cloudnative.fitapp.service.BodyweightService;
import java.util.List;
import java.util.Optional;


public class BodyweightFunctions extends BaseFunctionHandler {


    // Get all bodyweight entries - GET /api/bodyweight
    @FunctionName("GetAllBodyweight")
    public HttpResponseMessage getAllBodyweight(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "bodyweight",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all bodyweight entries");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            BodyweightService bodyweightService = new BodyweightService(cosmosDBService);
            List<Bodyweight> bodyweights = bodyweightService.getAllBodyweight();
            return createResponse(request, bodyweights);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Get bodyweight by user ID - GET /api/bodyweight/{id}
    @FunctionName("GetBodyweightByUserId")
    public HttpResponseMessage getBodyweightByUserId(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "bodyweight/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting bodyweight for user: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            BodyweightService bodyweightService = new BodyweightService(cosmosDBService);
            List<Bodyweight> bodyweights = bodyweightService.getBodyweightByUserId(Long.parseLong(id));
            return createResponse(request, bodyweights);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }


    // Add bodyweight entry - POST /api/bodyweight/add/{userId}
    @FunctionName("AddBodyweight")
    public HttpResponseMessage addBodyweight(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "bodyweight/add/{userId}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Adding bodyweight for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            Bodyweight bodyweight = parseBody(request, Bodyweight.class);
            BodyweightService bodyweightService = new BodyweightService(cosmosDBService);
            Bodyweight newBodyweight = bodyweightService.addBodyweight(Long.parseLong(userId), bodyweight);
            return createResponse(request, newBodyweight);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}