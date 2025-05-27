package cloudnative.fitapp.functions;

import cloudnative.fitapp.service.UserService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.dto.UpdatePasswordRequest;

import java.util.List;
import java.util.Optional;

/**
 * Simplified user management functions.
 */
public class UserFunctions extends BaseFunctionHandler {

    /**
     * Get all users - GET /api/users
     */
    @FunctionName("GetAllUsers")
    public HttpResponseMessage getAllUsers(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "users",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all users");

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);
            UserService userService = serviceFactory.getUserService();
            List<User> users = userService.getAllUsers();
            return createResponse(request, users);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get user by ID - GET /api/users/{id}
     */
    @FunctionName("GetUserById")
    public HttpResponseMessage getUserById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "users/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting user by ID: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            UserService userService = serviceFactory.getUserService();
            User user = userService.getUserById(id);

            if (user == null) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "User not found");
            }

            return createResponse(request, user);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Get user workouts - GET /api/users/{id}/workouts
     */
    @FunctionName("GetUserWorkouts")
    public HttpResponseMessage getUserWorkouts(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.OPTIONS},
                    route = "users/{id}/workouts",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting workouts for user: " + id);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            UserService userService = serviceFactory.getUserService();
            List<Workout> workouts = userService.getAllWorkoutsForUser(id);

            return createResponse(request, workouts);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update streak goal - PUT /api/users/{userId}/streakGoal/{streakGoal}
     */
    @FunctionName("UpdateStreakGoal")
    public HttpResponseMessage updateStreakGoal(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "users/{userId}/streakGoal/{streakGoal}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            @BindingName("streakGoal") String streakGoalStr,
            final ExecutionContext context) {

        context.getLogger().info("Updating streak goal for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            Integer streakGoal = Integer.parseInt(streakGoalStr);
            if (streakGoal < 0 || streakGoal > 7) {
                throw new IllegalArgumentException("Streak goal must be between 0 and 7");
            }

            // Find user
            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", userId);
            List<User> users = cosmosDBService.query("users", query, User.class);

            if (users.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "User not found");
            }

            User user = users.get(0);
            user.setStreakGoal(streakGoal);

            cosmosDBService.update("users", user, user.getEmail(), User.class);

            return createResponse(request, streakGoal);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update streak progress - PUT /api/users/{userId}/streakProgress
     */
    @FunctionName("UpdateStreakProgress")
    public HttpResponseMessage updateStreakProgress(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "users/{userId}/streakProgress",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Updating streak progress for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            // Find user
            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", userId);
            List<User> users = cosmosDBService.query("users", query, User.class);

            if (users.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "User not found");
            }

            User user = users.get(0);
            user.setStreakProgress(user.getStreakProgress() + 1);

            cosmosDBService.update("users", user, user.getEmail(), User.class);

            return createResponse(request, user.getStreakProgress());
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update password - PUT /api/users/{userId}/password
     */
    @FunctionName("UpdatePassword")
    public HttpResponseMessage updatePassword(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT, HttpMethod.OPTIONS},
                    route = "users/{userId}/password",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Updating password for user: " + userId);

        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return handleCors(request);
        }

        try {
            validateToken(request);

            UpdatePasswordRequest passwordRequest = parseBody(request, UpdatePasswordRequest.class);

            // Find user
            String query = String.format("SELECT * FROM c WHERE c.id = '%s'", userId);
            List<User> users = cosmosDBService.query("users", query, User.class);

            if (users.isEmpty()) {
                return createErrorResponse(request, HttpStatus.NOT_FOUND, "User not found");
            }

            User user = users.get(0);

            if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
                throw new SecurityException("Current password is incorrect");
            }

            if (passwordRequest.getNewPassword().length() < 8) {
                throw new IllegalArgumentException("New password must be at least 8 characters long");
            }

            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            cosmosDBService.update("users", user, user.getEmail(), User.class);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}