package cloudnative.fitapp.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.dto.UpdatePasswordRequest;
import cloudnative.fitapp.service.UserService;
import java.util.List;
import java.util.Optional;

/**
 * Azure Functions for user management endpoints.
 */
public class UserFunctions extends BaseFunctionHandler {

    /**
     * Get all users - GET /api/users
     */
    @FunctionName("GetAllUsers")
    public HttpResponseMessage getAllUsers(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    route = "users",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Getting all users");

        try {
            validateToken(request);
            UserService userService = getBean(UserService.class);
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
                    methods = {HttpMethod.GET},
                    route = "users/{id}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting user by ID: " + id);

        try {
            validateToken(request);
            UserService userService = getBean(UserService.class);
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
                    methods = {HttpMethod.GET},
                    route = "users/{id}/workouts",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Getting workouts for user: " + id);

        try {
            validateToken(request);
            UserService userService = getBean(UserService.class);
            List<Workout> workouts = userService.getAllWorkoutsForUser(id);
            return createResponse(request, workouts);
        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    /**
     * Update user - PUT /api/users/{email}
     */
    @FunctionName("UpdateUser")
    public HttpResponseMessage updateUser(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    route = "users/{email}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("email") String email,
            final ExecutionContext context) {

        context.getLogger().info("Updating user: " + email);

        try {
            validateToken(request);
            User user = parseBody(request, User.class);
            UserService userService = getBean(UserService.class);
            User updatedUser = userService.updateUser(email, user);
            return createResponse(request, updatedUser);
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
                    methods = {HttpMethod.PUT},
                    route = "users/{userId}/streakGoal/{streakGoal}",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            @BindingName("streakGoal") Integer streakGoal,
            final ExecutionContext context) {

        context.getLogger().info("Updating streak goal for user: " + userId);

        try {
            validateToken(request);
            UserService userService = getBean(UserService.class);
            userService.updateStreakGoal(userId, streakGoal);
            Integer newStreakGoal = userService.getUserById(userId).getStreakGoal();
            return createResponse(request, newStreakGoal);
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
                    methods = {HttpMethod.PUT},
                    route = "users/{userId}/streakProgress",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Updating streak progress for user: " + userId);

        try {
            validateToken(request);
            UserService userService = getBean(UserService.class);
            userService.completedWorkout(userId);
            Integer streakProgress = userService.getUserById(userId).getStreakProgress();
            return createResponse(request, streakProgress);
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
                    methods = {HttpMethod.PUT},
                    route = "users/{userId}/password",
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<String> request,
            @BindingName("userId") String userId,
            final ExecutionContext context) {

        context.getLogger().info("Updating password for user: " + userId);

        try {
            validateToken(request);
            UpdatePasswordRequest passwordRequest = parseBody(request, UpdatePasswordRequest.class);
            UserService userService = getBean(UserService.class);
            userService.updatePassword(userId,
                    passwordRequest.getCurrentPassword(),
                    passwordRequest.getNewPassword());

            return request.createResponseBuilder(HttpStatus.OK).build();
        } catch (Exception e) {
            return handleException(request, e);
        }
    }
}