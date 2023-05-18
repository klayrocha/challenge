package com.dws.challenge.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorList = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errorList.toString(), ex));
    }

    @ExceptionHandler(TransactionRuleException.class)
    private ResponseEntity<Object> transactionRuleException(TransactionRuleException dex) {
        logger.warn("Error " + dex.getMessage(), dex);
        String msgError = dex.getMessage();
        if (msgError.contains(":")) {
            msgError = msgError.substring(msgError.indexOf(":") + 1);
        }
        return new ResponseEntity<>(new ApiError(HttpStatus.EXPECTATION_FAILED, msgError, dex),
                HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    private ResponseEntity<Object> accountNotFoundException(AccountNotFoundException dex) {
        logger.warn("Error " + dex.getMessage(), dex);
        String msgError = dex.getMessage();
        if (msgError.contains(":")) {
            msgError = msgError.substring(msgError.indexOf(":") + 1);
        }
        return new ResponseEntity<>(new ApiError(HttpStatus.EXPECTATION_FAILED, msgError, dex),
                HttpStatus.EXPECTATION_FAILED);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
