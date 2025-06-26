package com.pm.userservice.exception;

import com.pm.userservice.exception.payload.ExceptionMsg;
import com.pm.userservice.exception.wrapper.AddressNotFoundException;
import com.pm.userservice.exception.wrapper.CredentialNotFoundException;
import com.pm.userservice.exception.wrapper.UserObjectNotFoundException;
import com.pm.userservice.exception.wrapper.VerificationTokenNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.BindException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private ExceptionMsg buildExceptionMsg(HttpStatus status, String message, String path, String errorCode) {
        return ExceptionMsg.builder()
                .timestamp(ZonedDateTime.now(ZONE_ID))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .errorCode(errorCode)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMsg> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e,
            final HttpServletRequest request) {

        log.warn("Validation error: {}", e.getMessage());

        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "Validation failed";

        var response = buildExceptionMsg(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI(),
                "VALIDATION_FAILED"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionMsg> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e,
            final HttpServletRequest request) {

        log.warn("Invalid request body: {}", e.getMessage());

        var response = buildExceptionMsg(
                HttpStatus.BAD_REQUEST,
                "Invalid request body format",
                request.getRequestURI(),
                "INVALID_REQUEST_BODY"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            UserObjectNotFoundException.class,
            CredentialNotFoundException.class,
            VerificationTokenNotFoundException.class,
            AddressNotFoundException.class
    })
    public ResponseEntity<ExceptionMsg> handleCustomNotFoundException(
            final RuntimeException e,
            final HttpServletRequest request) {

        log.warn("Entity not found: {}", e.getMessage());

        var response = buildExceptionMsg(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                request.getRequestURI(),
                "NOT_FOUND"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMsg> handleGeneralException(
            final Exception e,
            final HttpServletRequest request) {

        log.error("Unhandled exception: ", e);

        var response = buildExceptionMsg(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
