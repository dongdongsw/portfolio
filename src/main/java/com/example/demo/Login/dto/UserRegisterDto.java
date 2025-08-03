package com.example.demo.Login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    private String loginid;  // 아이디
    private String loginpw;  // 비밀번호
    private String nickname;  // 닉네임
    private String email;  // 이메일


}
