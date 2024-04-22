package com.ksh.purchase.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserType {
    String message() default "유효하지 않는 회원 유형입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
