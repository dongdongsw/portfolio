package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private String loginid;
    private String nickname;
    private String content;
    private String title;
    private String imagepath0;
    private String imagepath1;
    private String imagepath2;
    private String imagepath3;
    private String imagepath4;
}