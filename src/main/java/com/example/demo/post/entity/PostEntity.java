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

    @Column(name = "login_id", nullable = false, length = 25)
    private String loginid;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "content", length = 2048)
    private String content;

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDateTime modifydate;

    @Column(name = "view_count")
    private int viewcount;

    @CreatedDate
    @Column(name = "upload_date")
    private LocalDateTime uploaddate;

    @Column(name = "title", length = 24)
    private String title;

    @Column(name = "image_path0")
    private String imagepath0;

    @Column(name = "image_path1")
    private String imagepath1;

    @Column(name = "image_path2")
    private String imagepath2;

    @Column(name = "image_path3")
    private String imagepath3;

    @Column(name = "image_path4")
    private String imagepath4;

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

    public int getViewcount() { return viewcount; }
    public void setViewcount(int viewcount) { this.viewcount = viewcount; }

    public LocalDateTime getUploaddate() { return uploaddate; }
    public void setUploaddate(LocalDateTime uploaddate) { this.uploaddate = uploaddate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImagepath0() { return imagepath0; }
    public void setImagepath0(String imagepath0) { this.imagepath0 = imagepath0; }

    public String getImagepath1() { return imagepath1; }
    public void setImagepath1(String imagepath1) { this.imagepath1 = imagepath1; }

    public String getImagepath2() { return imagepath2; }
    public void setImagepath2(String imagepath2) { this.imagepath2 = imagepath2; }

    public String getImagepath3() { return imagepath3; }
    public void setImagepath3(String imagepath3) { this.imagepath3 = imagepath3; }

    public String getImagepath4() { return imagepath4; }
    public void setImagepath4(String imagepath4) { this.imagepath4 = imagepath4; }
}
