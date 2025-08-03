package com.example.demo.Comment.Dto;

public class CommentRequestDto {
    private int postId;
    private String loginId;
    private String nickname;
    private String content;
    private String author;

    public CommentRequestDto() {}

    public CommentRequestDto(int postId, String loginId, String nickname, String content, String author) {
        this.postId = postId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.content = content;
        this.author = author;
    }

    // Getter & Setter
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}