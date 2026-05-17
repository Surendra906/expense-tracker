package com.example.expense_tracker.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse toErrorResponse(HttpStatus status, String message) {
        return new ErrorResponse(Instant.now().toString(), status.value(), message);
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ExpenseNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String msg = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return new ResponseEntity<>(toErrorResponse(status, msg), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(toErrorResponse(status, message), status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(toErrorResponse(status, message), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(toErrorResponse(status, "Internal server error"), status);
    }
}
