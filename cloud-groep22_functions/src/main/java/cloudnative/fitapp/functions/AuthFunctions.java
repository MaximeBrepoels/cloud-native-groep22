package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.dto.*;
import cloudnative.fitapp.service.AuthService;
import cloudnative.fitapp.service.UserService;
import cloudnative.fitapp.domain.User;

/**
 * Azure Functions for authentication endpoints.
 * Handles user login and registration.
 */
public class AuthFunctions extends BaseFunctionHandler {

    /**
     * Login function - POST /api/auth/login
     */
    @FunctionName("AuthLogin")
    public HttpResponseMessage login(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    route = "auth/login",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Processing login request");

        try {
            LoginRequest loginRequest = parseBody(request, LoginRequest.class);
            AuthService authService = getBean(AuthService.class);
            UserService userService = getBean(UserService.class);

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
                    methods = {HttpMethod.POST},
                    route = "auth/register",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Processing registration request");

        try {
            RegisterRequest registerRequest = parseBody(request, RegisterRequest.class);
            AuthService authService = getBean(AuthService.class);

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