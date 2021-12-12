package jp.co.axa.apidemo.controllers.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * A controller advice handling {@link NoSuchElementException}.
 */
@ControllerAdvice
public class NoSuchElementAdvice {
    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handler(NoSuchElementException ex) {
        return ex.getMessage();
    }
}
