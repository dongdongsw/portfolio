package com.example.demo.Login.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDto {


    private String loginid;  // 아이디
    private String loginpw;  // 비밀번호
    private String nickname;

    private String phone;
    private LocalDate birthday;
    private String location;
    private String imagePath;



}
