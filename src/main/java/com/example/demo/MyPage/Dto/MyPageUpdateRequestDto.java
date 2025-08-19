package com.example.demo.MyPage.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor // Lombok이 기본 생성자를 자동으로 만듬
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 만들어줘
public class MyPageUpdateRequestDto {

    private String newNickname;

    private String currentPassword; // 현재 비밀번호
    private String newPassword;     // 새 비밀번호
    private String newPasswordConfirm; // 새 비밀번호 확인

    private String newEmail;            // 새 이메일 주소
    private String emailVerificationCode; // 이메일 인증번호
}