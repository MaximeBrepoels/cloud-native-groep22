package cloudnative.fitapp.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import cloudnative.fitapp.security.JwtUtil;
import cloudnative.fitapp.service.CosmosDBService;
import java.util.Optional;
import java.util.logging.Logger;


public abstract class BaseFunctionHandler {

    protected static final Logger logger = Logger.getLogger(BaseFunctionHandler.class.getName());
    protected static final ObjectMapper objectMapper = createObjectMapper();

    // Initialize services as static to reuse across function invocations
    protected static CosmosDBService cosmosDBService;
    protected static JwtUtil jwtUtil;

    static {
        try {
            cosmosDBService = CosmosDBService.getInstance();

            // Ensure containers exist
            cosmosDBService.ensureContainersExist();

            jwtUtil = new JwtUtil();
            logger.info("Services initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize services: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Service initialization failed", e);
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }


    // Validate JWT token from the Authorization header.
    protected String validateToken(HttpRequestMessage<?> request) {
        String authHeader = request.getHeaders().get("authorization");
        if (authHeader == null) {
            authHeader = request.getHeaders().get("Authorization");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid token");
        }

        return jwtUtil.extractEmail(token);
    }


    // Parses a request's JSON body into the specified class
    protected <T> T parseBody(HttpRequestMessage<?> request, Class<T> clazz) {
        try {
            String body = request.getBody().toString();
            logger.info("Parsing body: " + body);
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            logger.severe("Error parsing body: " + e.getMessage());
            throw new IllegalArgumentException("Invalid request body: " + e.getMessage());
        }
    }


    // Creates a success response with a JSON body
    protected HttpResponseMessage createResponse(HttpRequestMessage<?> request, Object body, HttpStatus status) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return request.createResponseBuilder(status)
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .body(json)
                    .build();
        } catch (Exception e) {
            logger.severe("Error serializing response: " + e.getMessage());
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error serializing response");
        }
    }

    protected HttpResponseMessage createResponse(HttpRequestMessage<?> request, Object body) {
        return createResponse(request, body, HttpStatus.OK);
    }


    // Creates an error response
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


    // Handles exceptions and returns appropriate HTTP responses
    protected HttpResponseMessage handleException(HttpRequestMessage<?> request, Exception e) {
        logger.severe("Error processing request: " + e.getMessage());
        e.printStackTrace();

        if (e instanceof SecurityException) {
            return createErrorResponse(request, HttpStatus.UNAUTHORIZED, e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            return createErrorResponse(request, HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + e.getMessage());
        }
    }


    // Gets the optional query parameter(s) from a request
    protected Optional<String> getQueryParam(HttpRequestMessage<?> request, String paramName) {
        return Optional.ofNullable(request.getQueryParameters().get(paramName));
    }


    // Handles CORS preflight requests
    protected HttpResponseMessage handleCors(HttpRequestMessage<?> request) {
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }
}