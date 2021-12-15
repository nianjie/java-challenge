package jp.co.axa.apidemo.controllers.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A controller advice rendering exceptions into user-friendly responses.
 * <br>
 * Targeted exceptions are:
 * <ul>
 *  <li>{@link NoSuchElementException}.</li>
 *  <li>{@link BindException}</li>
 * </ul>
 */
@RestControllerAdvice
public class ApiDemoExceptionAdvice {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNSE(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(LocalDateTime.now(), ex.getMessage(), Collections.emptyList()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBE(BindException ex) {
        List<String> validationErrors = Collections.emptyList();
        if (ex.hasErrors()) {
            validationErrors = new ArrayList<>(ex.getErrorCount());
            // extract validation error message per field.
            for (FieldError error: ex.getFieldErrors()) {
                validationErrors.add(String.format("%s : %s", error.getField(), error.getDefaultMessage()));
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(), "Posted data is not valid.", validationErrors));
    }

    /**
     * A JSON representation showing a REST-API error.
     */
    @Data
    static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final String message;
        private final List<String> errors;
    }
}
