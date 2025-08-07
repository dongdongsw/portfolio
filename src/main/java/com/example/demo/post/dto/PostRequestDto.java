/*
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
}*/
package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile; // 이 부분을 추가합니다.

import java.util.List; // List를 사용하기 위해 추가

@Getter
@Setter
public class PostRequestDto {
    private String loginid;
    private String nickname;
    private String content;
    private String title;

    // List로 변경하여 여러 파일을 받을 수 있도록 합니다.
    // HTML 폼에서 input name을 'files'로 맞춥니다.
    private List<MultipartFile> files; // List로 변경

    // 이전에 있던 imagepath 필드들은 삭제하거나,
    // 필요에 따라 DTO 역할 구분을 위해 남겨둘 수 있습니다.
    // 현재는 파일을 직접 받으므로 아래 필드들은 주석 처리하거나 삭제합니다.
    /*
    private String imagepath0;
    private String imagepath1;
    private String imagepath2;
    private String imagepath3;
    private String imagepath4;
    */
}