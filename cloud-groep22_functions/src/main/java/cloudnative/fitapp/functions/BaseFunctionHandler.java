package cloudnative.fitapp.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import cloudnative.fitapp.config.FunctionConfiguration;
import cloudnative.fitapp.security.JwtUtil;
import cloudnative.fitapp.exception.AuthServiceException;
import cloudnative.fitapp.exception.UserServiceException;
import cloudnative.fitapp.exception.ExerciseServiceException;
import cloudnative.fitapp.exception.WorkoutServiceException;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Base class for all Azure Functions.
 * Handles Spring context initialization, JWT validation, and error handling.
 */
public abstract class BaseFunctionHandler {

    protected static ApplicationContext context;
    protected static final Logger logger = Logger.getLogger(BaseFunctionHandler.class.getName());

    static {
        // Initialize Spring context once for all functions
        if (context == null) {
            context = new AnnotationConfigApplicationContext(FunctionConfiguration.class);
        }
    }

    /**
     * Get a Spring bean from the application context.
     */
    protected <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * Validate JWT token from the Authorization header.
     * Returns the email of the authenticated user.
     */
    protected String validateToken(HttpRequestMessage<?> request) {
        String authHeader = request.getHeaders().get("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthServiceException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        JwtUtil jwtUtil = getBean(JwtUtil.class);

        if (!jwtUtil.validateToken(token)) {
            throw new AuthServiceException("Invalid token");
        }

        return jwtUtil.extractEmail(token);
    }

    /**
     * Parse request body to the specified class.
     */
    protected <T> T parseBody(HttpRequestMessage<?> request, Class<T> clazz) {
        try {
            ObjectMapper mapper = getBean(ObjectMapper.class);
            String body = request.getBody().toString();
            return mapper.readValue(body, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request body: " + e.getMessage());
        }
    }

    /**
     * Create a success response with JSON body.
     */
    protected HttpResponseMessage createResponse(HttpRequestMessage<?> request, Object body) {
        try {
            ObjectMapper mapper = getBean(ObjectMapper.class);
            String json = mapper.writeValueAsString(body);
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(json)
                    .build();
        } catch (Exception e) {
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error serializing response");
        }
    }

    /**
     * Create an error response.
     */
    protected HttpResponseMessage createErrorResponse(HttpRequestMessage<?> request,
                                                      HttpStatus status, String message) {
        return request.createResponseBuilder(status)
                .header("Content-Type", "application/json")
                .body("{\"error\":\"" + message + "\"}")
                .build();
    }

    /**
     * Handle exceptions and return appropriate HTTP responses.
     */
    protected HttpResponseMessage handleException(HttpRequestMessage<?> request, Exception e) {
        logger.severe("Error processing request: " + e.getMessage());

        if (e instanceof AuthServiceException ||
                e instanceof UserServiceException ||
                e instanceof ExerciseServiceException ||
                e instanceof WorkoutServiceException) {
            return createErrorResponse(request, HttpStatus.UNAUTHORIZED, e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            return createErrorResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }
    }

    /**
     * Get path parameter from request.
     */
    protected String getPathParam(HttpRequestMessage<?> request, String paramName) {
        String value = request.getQueryParameters().get(paramName);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: " + paramName);
        }
        return value;
    }

    /**
     * Get optional query parameter from request.
     */
    protected Optional<String> getQueryParam(HttpRequestMessage<?> request, String paramName) {
        return Optional.ofNullable(request.getQueryParameters().get(paramName));
    }
}