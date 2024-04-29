package com.ksh.purchase.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원 관련
    USER_NOT_FOUND("회원을 찾을 수 없습니다.", NOT_FOUND),
    DUPLICATED_EMAIL("이미 가입된 이메일입니다.", BAD_REQUEST),
    MAIL_NOT_FOUND("메일을 찾을 수 없습니다.", NOT_FOUND),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", BAD_REQUEST),
    ADDRESS_NOT_FOUND("주소를 찾을 수 없습니다.", NOT_FOUND),
    UNAUTHORIZED_REQUEST("권한이 없는 사용자입니다.", UNAUTHORIZED),
    // 상품 관련
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다.", NOT_FOUND),
    PRODUCT_STOCK_LACK("상품 재고가 부족합니다.", BAD_REQUEST),
    // 메일 관련
    MAIL_SEND_ERROR("메일 전송에 실패했습니다.", INTERNAL_SERVER_ERROR),
    //암호화 관련
    ENCRYPTION_FAIL("암호화에 실패했습니다.", INTERNAL_SERVER_ERROR),
    DECRYPTION_FAIL("복호화에 실패했습니다.", INTERNAL_SERVER_ERROR),
    // 토큰 관련
    INVALID_TOKEN("유효하지 않은 토큰입니다.", UNAUTHORIZED),

    SERVER_ERROR("서버 에러가 발생했습니다.", INTERNAL_SERVER_ERROR);

    private final String description;
    private final HttpStatus status;
}
