package com.ksh.purchase.controller.reqeust;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserInfoUpdateRequest(
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 01x-xxxx-xxxx 형식으로 입력해주세요.")
        String phone,
        @NotBlank(message = "우편번호를 입력해주세요.")
        @Size(min = 5, max = 5, message = "우편번호는 5자리로 입력해주세요.")
        String zipcode,
        String address,
        String detailedAddress,
        Long addressId
) {
}
