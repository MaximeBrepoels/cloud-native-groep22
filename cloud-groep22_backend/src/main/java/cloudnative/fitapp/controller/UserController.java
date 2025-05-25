package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.domain.Workout;
import cloudnative.fitapp.dto.UpdatePasswordRequest;
import cloudnative.fitapp.exception.UserServiceException;
import cloudnative.fitapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(String.valueOf(id));
    }

    @GetMapping("/{id}/workouts")
    public List<Workout> getUserWorkouts(@PathVariable Long id) {
        try {
            return userService.getAllWorkoutsForUser(String.valueOf(id));
        } catch (UserServiceException e) {
            throw new UserServiceException(e.getMessage());
        }
    }

    @PutMapping("/{email}")
    public User updateUser(@PathVariable String email, @RequestBody User user) {
        return userService.updateUser(email, user);
    }

    @PutMapping("/{userId}/streakGoal/{streakGoal}")
    public Integer updateStreakGoal(@PathVariable Long userId, @PathVariable Integer streakGoal) {
        userService.updateStreakGoal(String.valueOf(userId), streakGoal);
        return userService.getUserById(String.valueOf(userId)).getStreakGoal();
    }

    @PutMapping("/{userId}/streakProgress")
    public Integer updateStreakProgress(@PathVariable Long userId) {
        userService.completedWorkout(String.valueOf(userId));
        return userService.getUserById(String.valueOf(userId)).getStreakProgress();
    }

    @PutMapping("/{userId}/password")
    public void updatePassword(@PathVariable Long userId, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(String.valueOf(userId), request.getCurrentPassword(), request.getNewPassword());
    }
}