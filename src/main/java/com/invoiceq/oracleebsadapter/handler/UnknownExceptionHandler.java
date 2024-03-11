package com.invoiceq.oracleebsadapter.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UnknownExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public void handleGenericException(Exception e) {
        LOGGER.error("Exception {}", e);
    }
}
