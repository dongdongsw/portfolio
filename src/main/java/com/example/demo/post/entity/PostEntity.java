package com.example.demo.post.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_entity")
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginid;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "content")
    private String content;

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDateTime modifydate;

    @CreatedDate
    @Column(name = "upload_date")
    private LocalDateTime uploaddate;

    @Column(name = "view_count")
    private int viewcount;

    @Column(name = "title")
    private String title;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ImageEntity imageEntity;

    // === Getter & Setter ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginid() { return loginid; }
    public void setLoginid(String loginid) { this.loginid = loginid; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getModifydate() { return modifydate; }
    public void setModifydate(LocalDateTime modifydate) { this.modifydate = modifydate; }

    public LocalDateTime getUploaddate() { return uploaddate; }
    public void setUploaddate(LocalDateTime uploaddate) { this.uploaddate = uploaddate; }

    public int getViewcount() { return viewcount; }
    public void setViewcount(int viewcount) { this.viewcount = viewcount; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ImageEntity getImageEntity() { return imageEntity; }
    public void setImageEntity(ImageEntity imageEntity) {
        this.imageEntity = imageEntity;
        imageEntity.setPost(this); // 양방향 관계 유지
    }
}
