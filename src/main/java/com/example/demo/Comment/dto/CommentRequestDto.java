package com.example.demo.Comment.dto;

public class CommentRequestDto {
    private int postId;
    private String loginId;
    private String nickname;
    private String content;

    public CommentRequestDto() {}

    public CommentRequestDto(int postId, String loginId, String nickname, String content) {
        this.postId = postId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.content = content;
    }


    // Getter & Setter
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }}