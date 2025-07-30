package com.example.demo.Comment.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @column(name = "login_id", length = 25, nullable = false)
    private String loginId;

    @column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @column(name = "content", length = 50)
    private String content;

    @column(name = "upload_date")
    private LocalDateTime uploadDate;

    @column(name = "modify_date")
    private LocalDateTime modifyDate;

    @column(name = "author")
    private LocalDateTime author;