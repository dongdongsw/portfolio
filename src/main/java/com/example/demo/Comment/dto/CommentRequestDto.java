package com.example.demo.Comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequestDto {
    private String loginId;
    private String nickname;

    @NotBlank(message = "content must not be blank")
    @Size(max = 1000, message = "content max length is 1000")
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