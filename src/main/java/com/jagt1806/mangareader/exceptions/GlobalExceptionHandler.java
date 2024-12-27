package com.jagt1806.mangareader.exceptions;

import com.jagt1806.mangareader.response.error.ErrorResponse;
import com.jagt1806.mangareader.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
    private MessageUtil messageUtil;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(messageUtil.getMessage("error.back"), status.value(), LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorResponse response = new ErrorResponse(
                messageUtil.getMessage("auth.error.access.denied"),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ImgNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImgNotFoundException(ImgNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(messageUtil.getMessage("img.not.found"), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse response = new ErrorResponse(messageUtil.getMessage("error.invalid.id", new Object[]{ex.getValue(), ex.getName()}), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UniqueException.class)
    public ResponseEntity<ErrorResponse> handleUniqueException(UniqueException ex) {
        ErrorResponse response = new ErrorResponse(
                messageUtil.getMessage(ex.getMessage()),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                messageUtil.getMessage("user.not.found"),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                messageUtil.getMessage("role.not.found"),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}