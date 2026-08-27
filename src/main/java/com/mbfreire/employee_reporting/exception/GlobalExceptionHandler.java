package com.mbfreire.employee_reporting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
           "erro", e.getMessage(),
           "timestamp", LocalDateTime.now()
        ));
    }

    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "erro", e.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }
}
