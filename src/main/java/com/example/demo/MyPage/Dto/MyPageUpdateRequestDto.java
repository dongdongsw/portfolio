package com.example.demo.MyPage.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

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

    private String phone; // 전화번호
    private String location; // 주소
    private LocalDate birthday; // 생년월일
    private String imagePath; //
}