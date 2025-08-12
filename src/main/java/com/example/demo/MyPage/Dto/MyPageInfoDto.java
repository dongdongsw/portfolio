package com.example.demo.MyPage.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageInfoDto {

    private String loginid;
    private String loginpw;
    private String nickname;
    private LocalDateTime createdAt;




}
