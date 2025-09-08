package com.example.demo.Comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequestDto {
    @NotBlank(message = "ID를 입력하세요")
    private String loginId;

    @NotBlank(message = "닉네임을 입력하세요")
    private String nickname;

    @NotBlank(message = "내용을 입력하세요")
    @Size(max = 300, message = "글자수는 최대 300자까지만 입력이 가능합니다")
    private String content;

    public CommentRequestDto() {}

    public CommentRequestDto(String loginId, String nickname, String content) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.content = content;
    }

    // Getter & Setter
    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }}