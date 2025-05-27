package cloudnative.fitapp.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Main configuration class for Azure Functions with Spring Boot.
 * This class initializes the Spring context that will be used by all Azure Functions.
 */
@SpringBootApplication
@ComponentScan(basePackages = "cloudnative.fitapp")
public class FunctionConfiguration {

    /**
     * Main method to start Spring Boot application context.
     * In Azure Functions, this is called once during cold start.
     */
    public static void main(String[] args) {
        SpringApplication.run(FunctionConfiguration.class, args);
    }

    /**
     * Password encoder bean for encrypting user passwords.
     * We use BCrypt for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ObjectMapper bean configured for proper JSON serialization.
     * This ensures dates and other objects are serialized correctly.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}