package com.ksh.purchase.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ksh.purchase.exception.ErrorCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.info("{} is occurred.", e.getStatus());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getStatus().value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(SERVER_ERROR.getStatus().value(), SERVER_ERROR.getDescription()));

    }
}
