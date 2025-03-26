package org.personal.poc.events.observability.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiDefaultExceptionHandler {


    @ExceptionHandler(exception = UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
