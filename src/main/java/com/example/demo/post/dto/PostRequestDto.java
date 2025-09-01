package com.example.demo.post.dto;// PostRequestDto.java
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter @Setter
public class PostRequestDto {
    private String loginid;
    private String nickname;
    private String title;
    private String content;
    private List<MultipartFile> files;

    // 🔴 수정 시, 최종 이미지 순서를 JSON(String)으로 받음
    // e.g. [{"type":"existing","path":"/images/abc.webp"},{"type":"new","index":0}]
    private String order;
}
