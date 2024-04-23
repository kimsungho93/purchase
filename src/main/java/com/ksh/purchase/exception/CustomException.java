package com.ksh.purchase.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException {

    private String message;
    private HttpStatus status;

    public CustomException(ErrorCode errorCode) {
        this.message = errorCode.getDescription();
        this.status = errorCode.getStatus();
    }
}
