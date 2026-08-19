package VV.dev.notificator.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorMessageResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                "Authorization header is required",
                "Please provide a valid JWT token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessageResponse);
    }
}
