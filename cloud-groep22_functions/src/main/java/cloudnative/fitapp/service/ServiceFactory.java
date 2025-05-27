package cloudnative.fitapp.service;

import cloudnative.fitapp.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Factory class to create and manage service instances for Azure Functions.
 * This replaces Spring's dependency injection.
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    // Singleton services
    private final CosmosDBService cosmosDBService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthService authService;
    private final WorkoutService workoutService;

    private ServiceFactory() {
        // Initialize services in dependency order
        this.cosmosDBService = CosmosDBService.getInstance();
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = new JwtUtil();
        this.userService = new UserService(cosmosDBService, passwordEncoder);
        this.authService = new AuthService(jwtUtil, cosmosDBService, userService, passwordEncoder);
        this.workoutService = new WorkoutService(cosmosDBService, userService);
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public CosmosDBService getCosmosDBService() {
        return cosmosDBService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public JwtUtil getJwtUtil() {
        return jwtUtil;
    }

    public UserService getUserService() {
        return userService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public WorkoutService getWorkoutService() {
        return workoutService;
    }
}