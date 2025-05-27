package cloudnative.fitapp.service;

import cloudnative.fitapp.security.JwtUtil;
import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.exception.AuthServiceException;
import cloudnative.fitapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String email, String password) {
        if (email == "" || password == "") {
            throw new AuthServiceException("Email and password are required");
        }

        // Use the new method that returns a List
        List<User> users = userRepository.findByEmail(email);
        User user = users.isEmpty() ? null : users.get(0);

        if (user == null) {
            throw new AuthServiceException("User with email " + email + " not found");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthServiceException("Invalid password");
        } else {
            return jwtUtil.generateToken(user);
        }
    }

    public User register(String name, String email, String password) {

        if (name == "" || email == "" || password == "") {
            throw new AuthServiceException("Name, email, and password are required");
        }

        if (!name.matches("^[a-zA-Z ]+$")) {
            throw new AuthServiceException("Name can only contain letters and spaces");
        }

        // Use the new method that returns a List
        List<User> existingUsers = userRepository.findByEmail(email);
        if (!existingUsers.isEmpty()) {
            throw new AuthServiceException("Email is already in use");
        }

        if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            throw new AuthServiceException("Invalid email format");
        }

        if (password.length() < 8) {
            throw new AuthServiceException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new AuthServiceException("Password must contain at least one number");
        } else if (!password.matches(".*[a-z].*")) {
            throw new AuthServiceException("Password must contain at least one lowercase letter");
        } else if (!password.matches(".*[A-Z].*")) {
            throw new AuthServiceException("Password must contain at least one uppercase letter");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return userService.createUser(name, email, encodedPassword);
    }
}