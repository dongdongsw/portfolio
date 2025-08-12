package com.example.demo.Login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDto {

    private String loginid;
    private String loginpw;
    private String email;
    private String nickname;
}