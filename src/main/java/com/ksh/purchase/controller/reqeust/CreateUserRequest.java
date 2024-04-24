package com.ksh.purchase.controller.reqeust;

import com.ksh.purchase.entity.Address;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.entity.enums.UserType;
import com.ksh.purchase.util.ValidUserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;
import java.util.ArrayList;

@Builder
public record CreateUserRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        String password,

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 01x-xxxx-xxxx 형식으로 입력해주세요.")
        String phone,

        @NotBlank(message = "우편번호를 입력해주세요.")
        @Size(min = 5, max = 5, message = "우편번호는 5자리로 입력해주세요.")
        String zipcode,
        @NotBlank(message = "주소를 입력해주세요.")
        String address,
        String detailedAddress,

        @ValidUserType
        String userType
) implements Serializable {

    public User toUserEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phone(phone)
                .userType(UserType.valueOf(userType))
                .addressList(new ArrayList<>())
                .productList(new ArrayList<>())
                .build();
    }

    public Address toAddressEntity(User user) {
        return Address.builder()
                .user(user)
                .zipcode(zipcode)
                .address(address)
                .detailedAddress(detailedAddress)
                .build();
    }

}
