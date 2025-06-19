package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.dto.*;
import cloudnative.fitapp.service.AuthService;
import cloudnative.fitapp.service.UserService;
import cloudnative.fitapp.security.SimplePasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class AuthFunctions extends BaseFunctionHandler {

    private static final Logger logger = Logger.getLogger(AuthFunctions.class.getName());


    // Register new user - POST /api/auth/register
    @FunctionName("Register")
    public HttpResponseMessage register(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "auth/register",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Processing registration request");

        // Handle CORS preflight
        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            RegisterRequest registerRequest = parseBody(request, RegisterRequest.class);
            context.getLogger().info("Parsed register request for email: " + registerRequest.getEmail());

            // Create services with SimplePasswordEncoder
            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            AuthService authService = new AuthService(jwtUtil, cosmosDBService, userService, passwordEncoder);

            context.getLogger().info("Created services, attempting registration...");

            User user = authService.register(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );

            context.getLogger().info("Registration completed, user object: " + (user != null ? "exists" : "null"));

            if (user == null) {
                context.getLogger().severe("Registration returned null user!");
                throw new RuntimeException("Registration failed - user creation returned null");
            }

            context.getLogger().info("User ID: " + user.getId() + ", Name: " + user.getName());

            UserResponse response = new UserResponse(user);
            context.getLogger().info("Created UserResponse successfully");

            return createResponse(request, response, HttpStatus.CREATED);

        } catch (Exception e) {
            context.getLogger().severe("Registration error: " + e.getMessage());
            e.printStackTrace();
            return handleException(request, e);
        }
    }


    // Login user - POST /api/auth/login
    @FunctionName("Login")
    public HttpResponseMessage login(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    route = "auth/login",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Processing login request");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            LoginRequest loginRequest = parseBody(request, LoginRequest.class);

            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Email and password are required");
            }

            SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            AuthService authService = new AuthService(jwtUtil, cosmosDBService, userService, passwordEncoder);

            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail());

            if (user == null) {
                throw new RuntimeException("User not found after successful login");
            }

            AuthResponse response = new AuthResponse(token, user.getId());
            return createResponse(request, response);

        } catch (Exception e) {
            context.getLogger().severe("Login error: " + e.getMessage());
            return handleException(request, e);
        }
    }


    // Test Cosmos DB connection - GET /api/test/cosmos
    @FunctionName("TestCosmosConnection")
    public HttpResponseMessage testConnection(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "test/cosmos",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            context.getLogger().info("Testing Cosmos DB connection...");

            // Test basic connection
            List<User> users = cosmosDBService.findAll("users", User.class);
            context.getLogger().info("Found " + users.size() + " users in database");

            // Test creating a simple user
            User testUser = new User("Test User", "test@example.com", "hashedpassword");
            testUser.setId("test-" + System.currentTimeMillis());

            User savedUser = cosmosDBService.save("users", testUser, testUser.getEmail(), User.class);
            context.getLogger().info("Test user saved: " + (savedUser != null ? savedUser.getId() : "null"));

            return createResponse(request, "Connection test successful. Users: " + users.size());
        } catch (Exception e) {
            context.getLogger().severe("Connection test failed: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
