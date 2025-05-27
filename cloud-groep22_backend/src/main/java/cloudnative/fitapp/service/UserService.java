package cloudnative.fitapp.service;

import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.exception.UserServiceException;
import cloudnative.fitapp.repository.UserRepository;
import cloudnative.fitapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(String name, String email, String password) {
        // Use the new method that returns a List
        List<User> existingUsers = userRepository.findByEmail(email);
        if (!existingUsers.isEmpty()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User newUser = new User(name, email, password);
        newUser.setId(String.valueOf(System.currentTimeMillis())); // Use String
        return userRepository.save(newUser);
    }

    public User getUserById(String id) { // Use String
        return userRepository.findById(id).orElse(null);
    }

    public boolean deleteUser(String id) { // Use String
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Workout> getAllWorkoutsForUser(String userId) { // Use String
        User user = getUserById(userId);
        if (user == null) {
            throw new UserServiceException("User not found with ID: " + userId);
        }
        List<Workout> workouts = workoutRepository.findWorkoutsByUserId(userId);
        return workouts;
    }

    public void completedWorkout(String userId) { // Use String
        User user = getUserById(userId);
        if (user == null) {
            throw new UserServiceException("User not found with ID: " + userId);
        }
        user.setStreakProgress(user.getStreakProgress() + 1);
        userRepository.save(user);
    }

    public void updateStreakGoal(String userId, Integer streakGoal) { // Use String
        User user = getUserById(userId);
        if (user == null) {
            throw new UserServiceException("User not found with ID: " + userId);
        } else if (streakGoal < 0) {
            throw new UserServiceException("Streak goal must be at least 0");
        } else if (streakGoal > 7) {
            throw new UserServiceException("Streak goal must be at most 7");
        }
        user.setStreakGoal(streakGoal);
        userRepository.save(user);
    }

    public void updatePassword(String userId, String currentPassword, String newPassword) { // Use String
        User user = getUserById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserServiceException("Current password is incorrect");
        }
        if (newPassword.length() < 8) {
            throw new UserServiceException("New password must be at least 8 characters long");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        // Use the new method that returns a List
        List<User> users = userRepository.findByEmail(email);
        return users.isEmpty() ? null : users.get(0);
    }

    public List<User> getAllUsers() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public User updateUser(String email, User updatedUser) {
        // Use the new method that returns a List
        List<User> existingUsers = userRepository.findByEmail(email);
        if (existingUsers.isEmpty()) {
            throw new UserServiceException("User not found with email: " + email);
        }
        User existingUser = existingUsers.get(0);
        existingUser.setName(updatedUser.getName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setStreakGoal(updatedUser.getStreakGoal());
        existingUser.setStreakProgress(updatedUser.getStreakProgress());
        return userRepository.save(existingUser);
    }
}