package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String loginid;
    private String nickname;
    private String content;
    private String title;
    private String imagepath0;
    private String imagepath1;
    private String imagepath2;
    private String imagepath3;
    private String imagepath4;
    private int viewcount;
    private LocalDateTime uploaddate;
    private LocalDateTime modifydate;
}