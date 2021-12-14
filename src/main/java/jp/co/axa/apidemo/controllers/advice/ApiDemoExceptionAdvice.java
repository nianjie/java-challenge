package jp.co.axa.apidemo.controllers.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
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
    public ResponseEntity<Object> handlerNSE(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBE(BindException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(LocalDateTime.now(), ex.getMessage()));
    }

    @Data
    static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final String message;
    }
}
