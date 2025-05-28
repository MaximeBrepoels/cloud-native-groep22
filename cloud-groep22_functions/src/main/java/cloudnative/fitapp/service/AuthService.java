package cloudnative.fitapp.service;

import cloudnative.fitapp.security.JwtUtil;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.security.SimplePasswordEncoder;
import java.util.List;


public class AuthService {

    private final JwtUtil jwtUtil;
    private final CosmosDBService cosmosDBService;
    private final UserService userService;
    private final SimplePasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, CosmosDBService cosmosDBService,
                       UserService userService, SimplePasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.cosmosDBService = cosmosDBService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Email and password are required");
        }

        String query = String.format("SELECT * FROM c WHERE c.email = '%s'", email);
        List<User> users = cosmosDBService.query("users", query, User.class);
        User user = users.isEmpty() ? null : users.get(0);

        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user);
    }

    public User register(String name, String email, String password) {
        if (name == null || name.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new RuntimeException("Name, email, and password are required");
        }

        if (!name.matches("^[a-zA-Z ]+$")) {
            throw new RuntimeException("Name can only contain letters and spaces");
        }

        String query = String.format("SELECT * FROM c WHERE c.email = '%s'", email);
        List<User> existingUsers = cosmosDBService.query("users", query, User.class);
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("Email is already in use");
        }

        if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            throw new RuntimeException("Invalid email format");
        }

        if (password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new RuntimeException("Password must contain at least one number");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }

        String encodedPassword = passwordEncoder.encode(password);
        return userService.createUser(name, email, encodedPassword);
    }
}