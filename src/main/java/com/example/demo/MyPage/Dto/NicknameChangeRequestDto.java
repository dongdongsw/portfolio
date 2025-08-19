package com.example.demo.MyPage.Dto;

import jakarta.validation.constraints.NotBlank; // 비어있거나 공백만 있는 문자열을 허용하지 않음
import jakarta.validation.constraints.Size; // 문자열의 길이 제한

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameChangeRequestDto {
    @NotBlank(message = "새 닉네임을 입력해주세요.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String newNickname;
}