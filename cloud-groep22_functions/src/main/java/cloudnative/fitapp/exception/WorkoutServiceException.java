package cloudnative.fitapp.exception;

public class WorkoutServiceException extends RuntimeException {
    public WorkoutServiceException(String message) {
        super(message);
    }
}  
