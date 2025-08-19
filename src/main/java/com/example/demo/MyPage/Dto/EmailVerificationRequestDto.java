package com.example.demo.MyPage.Dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailVerificationRequestDto {
    @NotBlank(message = "인증번호를 입력해주세요.")
    private String code;
}