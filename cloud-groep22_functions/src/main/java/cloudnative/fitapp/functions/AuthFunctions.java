package cloudnative.fitapp.functions;

import cloudnative.fitapp.service.AuthService;
import cloudnative.fitapp.service.UserService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.dto.*;

import java.util.List;
import java.util.Optional;

/**
 * Simplified authentication functions without Spring Boot dependency.
 */
public class AuthFunctions extends BaseFunctionHandler {

    /**
     * Login function - POST /api/auth/login
     */
    @FunctionName("AuthLogin")
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

            AuthService authService = serviceFactory.getAuthService();
            UserService userService = serviceFactory.getUserService();

            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail());
            AuthResponse response = new AuthResponse(token, user.getId());

            return createResponse(request, response);

        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Register function - POST /api/auth/register
     */
    @FunctionName("AuthRegister")
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

            AuthService authService = serviceFactory.getAuthService();
            User user = authService.register(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );
            UserResponse response = new UserResponse(user);

            return createResponse(request, response);

        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}