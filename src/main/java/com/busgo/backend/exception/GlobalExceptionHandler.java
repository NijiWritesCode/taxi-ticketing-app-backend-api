package com.busgo.backend.exception;
import com.busgo.backend.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildError("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        log.error("Runtime Exception caught: ", ex);
        return buildError("VALIDATION_ERROR", ex.getMessage() != null ? ex.getMessage() : "NullPointerException occurred", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled: ", ex);
        return buildError("INTERNAL_ERROR", "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildError(String code, String message, HttpStatus status) {
        ErrorResponse res = ErrorResponse.builder()
            .error(ErrorResponse.ErrorDetail.builder()
                .code(code)
                .message(message)
                .build())
            .build();
        return new ResponseEntity<>(res, status);
    }
}
