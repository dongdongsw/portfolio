package com.example.demo.Login.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {


    private String loginid;  // 아이디
    private String loginpw;  // 비밀번호
    private String nickname;



}
