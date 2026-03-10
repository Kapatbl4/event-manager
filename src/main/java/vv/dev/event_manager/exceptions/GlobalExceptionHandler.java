package vv.dev.event_manager.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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
}
