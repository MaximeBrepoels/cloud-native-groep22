package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Pure Java User Service for Azure Functions (no Spring dependencies).
 */
public class UserService {

    private final CosmosDBService cosmosDBService;
    private final PasswordEncoder passwordEncoder;

    public UserService(CosmosDBService cosmosDBService, PasswordEncoder passwordEncoder) {
        this.cosmosDBService = cosmosDBService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name, String email, String password) {
        // Check if user already exists
        String query = String.format("SELECT * FROM c WHERE c.email = '%s'", email);
        List<User> existingUsers = cosmosDBService.query("users", query, User.class);

        if (!existingUsers.isEmpty()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User newUser = new User(name, email, password);
        newUser.setId(String.valueOf(System.currentTimeMillis()));
        return cosmosDBService.save("users", newUser, email, User.class);
    }

    public User getUserById(String id) {
        String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id);
        List<User> users = cosmosDBService.query("users", query, User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    public User getUserByEmail(String email) {
        String query = String.format("SELECT * FROM c WHERE c.email = '%s'", email);
        List<User> users = cosmosDBService.query("users", query, User.class);
        return users.isEmpty() ? null : users.get(0);
    }

    public boolean deleteUser(String id) {
        User user = getUserById(id);
        if (user != null) {
            cosmosDBService.deleteById("users", id, user.getEmail());
            return true;
        }
        return false;
    }

    public List<Workout> getAllWorkoutsForUser(String userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        String query = String.format("SELECT * FROM c WHERE c.userId = '%s'", userId);
        return cosmosDBService.query("workouts", query, Workout.class);
    }

    public void completedWorkout(String userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        user.setStreakProgress(user.getStreakProgress() + 1);
        cosmosDBService.update("users", user, user.getEmail(), User.class);
    }

    public void updateStreakGoal(String userId, Integer streakGoal) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        if (streakGoal < 0 || streakGoal > 7) {
            throw new RuntimeException("Streak goal must be between 0 and 7");
        }
        user.setStreakGoal(streakGoal);
        cosmosDBService.update("users", user, user.getEmail(), User.class);
    }

    public void updatePassword(String userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        if (newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters long");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        cosmosDBService.update("users", user, user.getEmail(), User.class);
    }

    public List<User> getAllUsers() {
        return cosmosDBService.findAll("users", User.class);
    }

    public User updateUser(String email, User updatedUser) {
        List<User> existingUsers = cosmosDBService.query("users",
                String.format("SELECT * FROM c WHERE c.email = '%s'", email), User.class);

        if (existingUsers.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        User existingUser = existingUsers.get(0);
        existingUser.setName(updatedUser.getName());
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getStreakGoal() != null) {
            existingUser.setStreakGoal(updatedUser.getStreakGoal());
        }
        if (updatedUser.getStreakProgress() != null) {
            existingUser.setStreakProgress(updatedUser.getStreakProgress());
        }

        return cosmosDBService.update("users", existingUser, existingUser.getEmail(), User.class);
    }
}