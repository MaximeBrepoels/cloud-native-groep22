package cloudnative.fitapp.functions;

import cloudnative.fitapp.service.ServiceFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import cloudnative.fitapp.security.JwtUtil;
import cloudnative.fitapp.service.CosmosDBService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Simplified base class for all Azure Functions without Spring Boot dependency.
 */
public abstract class BaseFunctionHandler {

    protected static final Logger logger = Logger.getLogger(BaseFunctionHandler.class.getName());
    protected static final ObjectMapper objectMapper = createObjectMapper();
    protected static final ServiceFactory serviceFactory = ServiceFactory.getInstance();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Validate JWT token from the Authorization header.
     */
    protected String validateToken(HttpRequestMessage<?> request) {
        String authHeader = request.getHeaders().get("authorization");
        if (authHeader == null) {
            authHeader = request.getHeaders().get("Authorization");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!serviceFactory.getJwtUtil().validateToken(token)) {
            throw new SecurityException("Invalid token");
        }

        return serviceFactory.getJwtUtil().extractEmail(token);
    }

    /**
     * Parse request body to the specified class.
     */
    protected <T> T parseBody(HttpRequestMessage<?> request, Class<T> clazz) {
        try {
            String body = request.getBody().toString();
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request body: " + e.getMessage());
        }
    }

    /**
     * Create a success response with JSON body.
     */
    protected HttpResponseMessage createResponse(HttpRequestMessage<?> request, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
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
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .body("{\"error\":\"" + message.replace("\"", "\\\"") + "\"}")
                .build();
    }

    /**
     * Handle exceptions and return appropriate HTTP responses.
     */
    protected HttpResponseMessage handleException(HttpRequestMessage<?> request, Exception e) {
        logger.severe("Error processing request: " + e.getMessage());
        e.printStackTrace();

        if (e instanceof SecurityException) {
            return createErrorResponse(request, HttpStatus.UNAUTHORIZED, e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            return createErrorResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }
    }

    /**
     * Get optional query parameter from request.
     */
    protected Optional<String> getQueryParam(HttpRequestMessage<?> request, String paramName) {
        return Optional.ofNullable(request.getQueryParameters().get(paramName));
    }

    /**
     * Handle CORS preflight requests.
     */
    protected HttpResponseMessage handleCors(HttpRequestMessage<?> request) {
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }
}