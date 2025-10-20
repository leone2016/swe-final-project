package edu.miu.blog.app.error;

import edu.miu.blog.app.error.exception.BusinessException;
import edu.miu.blog.app.error.exception.ResourceNotFoundException;
import edu.miu.blog.app.util.WrapWith;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@WrapWith("errors")
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage resourceNotFoundException(BusinessException ex, WebRequest request) {
        log.warn("Business exception occurred: {}", ex.getMessage());
        return new ErrorMessage(
                HttpStatus.UNPROCESSABLE_ENTITY,
                String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                ex.getMessage(),
                null
           );

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ErrorMessage(
                HttpStatus.NOT_FOUND,
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage(),
                null
        );

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "ERROR INTERNO",
                null);

    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> errorHandler(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());

        List<ValidationError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult()
                .getFieldErrors()) {
            log.debug("Field validation error - {}: {}", error.getField(), error.getDefaultMessage());
            ValidationError build = ValidationError.builder()
                    .code(error.getField())
                    .message(error.getDefaultMessage())
                    .build();
            errors.add(build);
        }
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST.name(), errors);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessage> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        log.warn("Response status exception occurred: {} - {}", ex.getStatusCode(), ex.getReason());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorMessage errorMessage = new ErrorMessage(
                status,
                String.valueOf(status.value()),
                ex.getReason(),
                null
        );
        return new ResponseEntity<>(errorMessage, status);
    }

}
