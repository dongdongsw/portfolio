package com.example.demo.Comment.Dto;

import java.time.LocalDateTime;

public class CommentResponseDto {
    private int id;
    private int postId;
    private String loginId;
    private String nickname;
    private String content;
    private LocalDateTime uploadDate;
    private LocalDateTime modifyDate;

    public CommentResponseDto() {}

    public CommentResponseDto(int id, int postId, String loginId, String nickname,
                              String content, LocalDateTime uploadDate, LocalDateTime modifyDate) {
        this.id = id;
        this.postId = postId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.content = content;
        this.uploadDate = uploadDate;
        this.modifyDate = modifyDate;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getDisplayedAt() {
        return (modifyDate != null ? modifyDate : uploadDate);
    }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public LocalDateTime getUpdatedAt() {
        return modifyDate;
    }
    public void setModifyDate(LocalDateTime modifyDate) { this.modifyDate = modifyDate; }
}