package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostRequestDto {
    private String loginid;
    private String nickname;
    private String content;
    private String title;
    private List<MultipartFile> files;
}