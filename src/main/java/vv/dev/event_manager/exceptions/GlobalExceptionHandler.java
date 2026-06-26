package vv.dev.event_manager.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleGeneralExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                "Internal server error",
                "Unexpected error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                ex.getMessage(),
                "Entity not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                ex.getMessage(),
                "Not valid argument"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessageResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                ex.getMessage(),
                "Not valid argument"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(";"));

        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                "Validation failed",
                errors.toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                ex.getMessage(),
                "Authorization denied"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessageResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                ex.getMessage(),
                "Access denied"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessageResponse);
    }

}
