package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.dto.*;
import cloudnative.fitapp.service.AuthService;
import cloudnative.fitapp.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication functions for Azure Functions.
 */
public class AuthFunctions extends BaseFunctionHandler {

    private static final Logger logger = Logger.getLogger(AuthFunctions.class.getName());

    /**
     * Login function - POST /api/auth/login
     */
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

        // Handle CORS preflight
        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            LoginRequest loginRequest = parseBody(request, LoginRequest.class);

            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Email and password are required");
            }

            // Create services directly instead of using factory pattern
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            AuthService authService = new AuthService(jwtUtil, cosmosDBService, userService, passwordEncoder);

            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail());

            if (user == null) {
                throw new RuntimeException("User not found after successful login");
            }

            AuthResponse response = new AuthResponse(token, user.getId());
            return createJsonResponse(request, response);

        } catch (Exception e) {
            context.getLogger().severe("Login error: " + e.getMessage());
            return handleException(request, e);
        }
    }

    /**
     * Register function - POST /api/auth/register
     */
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

            // Create services directly
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            UserService userService = new UserService(cosmosDBService, passwordEncoder);
            AuthService authService = new AuthService(jwtUtil, cosmosDBService, userService, passwordEncoder);

            User user = authService.register(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );

            UserResponse response = new UserResponse(user);
            return createJsonResponse(request, response, HttpStatus.CREATED);

        } catch (Exception e) {
            context.getLogger().severe("Registration error: " + e.getMessage());
            return handleException(request, e);
        }
    }
}