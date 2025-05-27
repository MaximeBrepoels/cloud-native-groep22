package cloudnative.fitapp.controller;

import cloudnative.fitapp.domain.User;
import cloudnative.fitapp.exception.AuthServiceException;
import cloudnative.fitapp.service.AuthService;
import cloudnative.fitapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cloudnative.fitapp.dto.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            User user = userService.getUserByEmail(loginRequest.getEmail());
            String userId = user.getId();
            return new AuthResponse(token, userId);
        } catch (AuthServiceException e) {
            throw new AuthServiceException(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.register(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());
            return ResponseEntity.ok(new UserResponse(user));
        } catch (AuthServiceException e) {
            throw new AuthServiceException(e.getMessage());
        }
    }
}