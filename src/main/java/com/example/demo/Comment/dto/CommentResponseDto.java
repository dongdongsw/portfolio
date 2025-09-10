package com.example.demo.Comment.dto;

import com.example.demo.Comment.Entity.CommentEntity;
import java.time.format.DateTimeFormatter;

public class CommentResponseDto {
    private int id;
    private int postId;
    private String loginId;
    private String nickname;
    private String content;
    private String uploadDate;
    private String modifyDate;

    public CommentResponseDto() {}

    public CommentResponseDto(CommentEntity comment, String latestNickname) {
        if(comment == null) throw new IllegalArgumentException("CommentEntity is null");

        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.loginId = comment.getLoginId();
        this.nickname = latestNickname != null ? latestNickname : "Unknown";
        this.content = comment.getContent();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.uploadDate = comment.getUploadDate() != null ? comment.getUploadDate().format(formatter) : null;
        this.modifyDate = comment.getModifyDate() != null ? comment.getModifyDate().format(formatter) : null;
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

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public String getModifyDate() { return  modifyDate; }
    public void setModifyDate(String modifyDate) { this.modifyDate = modifyDate; }
}
