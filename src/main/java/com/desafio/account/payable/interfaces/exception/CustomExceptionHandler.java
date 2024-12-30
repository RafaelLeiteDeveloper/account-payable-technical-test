package com.desafio.account.payable.interfaces.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class CustomExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Error> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest s) {
        log.error("method=DuplicateKeyException | message: {}", e.getMessage());
        return Error.response(e.getMessage(), HttpStatus.CONFLICT, s.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Error> handleNullPointerException(NullPointerException e, HttpServletRequest s) {
        log.error("method=NullPointerException | message: {}", e.getMessage());
        return Error.response(e.getMessage(), HttpStatus.BAD_REQUEST, s.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentInvalidException(MethodArgumentNotValidException e, HttpServletRequest s) {
        log.error("method=MethodArgumentNotValidException | message: {}", e.getMessage());
        var errorsMessage = this.getErrorMessage(e);
        return Error.response(errorsMessage, HttpStatus.BAD_REQUEST, s.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> entityNotFoundException(EntityNotFoundException e, HttpServletRequest s) {
        log.error("method=EntityNotFoundException | message: {}", e.getMessage());
        return Error.response(e.getMessage(), HttpStatus.CONFLICT, s.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidDateFormat(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest s){

        String message = ex.getMessage();
        Throwable cause = ex.getCause();
        message = this.isInvalidFormatException(cause, message);
        log.error("method=handleHttpMessageNotReadableExceptions | Error message:{} ", message);

        return Error.response(message, HttpStatus.BAD_REQUEST, s.getRequestURI());
    }

    private String getErrorMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream().map(error -> {
            String field = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            return field + " " + errorMessage;
        }).collect(Collectors.joining(", "));
    }

    private String isInvalidFormatException(Throwable cause, String message) {
        return (cause instanceof InvalidFormatException) ?
                String.format("Exceção de formato inválido: O campo contém um valor inválido: '%s'. Verifique se o valor está no formato correto.", ((InvalidFormatException) cause).getValue().toString()) :
                message;
    }

}