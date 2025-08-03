package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class PostRequestDto {
    private String loginid;
    private String nickname;
    private String content;
    private String title;
    private int postid1;
    private int postid2;
    private int postid3;
    private int postid4;
    private int postid5;
}
