package com.example.demo.post.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_entity")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginid;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "content")
    private String content;

    @Column(name = "modify_date")
    private LocalDateTime modifydate;

    @Column(name = "view_count")
    private int viewcount;

    @Column(name = "upload_date")
    private LocalDateTime uploaddate;

    @Column(name = "title")
    private String title;

    @Column(name = "post_id_1")
    private int postid1;

    @Column(name = "post_id_2")
    private int postid2;

    @Column(name = "post_id_3")
    private int postid3;

    @Column(name = "post_id_4")
    private int postid4;

    @Column(name = "post_id_5")
    private int postid5;

    // === Getter & Setter ===

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public String getloginid() {
        return loginid;
    }

    public void setloginid(String loginid) {
        this.loginid = loginid;
    }

    public String getnickname() {
        return nickname;
    }

    public void setnickname(String nickname) {
        this.nickname = nickname;
    }

    public String getcontent() {
        return content;
    }

    public void setcontent(String content) {
        this.content = content;
    }

    public LocalDateTime getmodifydate() {
        return modifydate;
    }

    public void setmodifydate(LocalDateTime modifydate) {
        this.modifydate = modifydate;
    }

    public int getviewcount() {
        return viewcount;
    }

    public void setviewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public LocalDateTime getuploaddate() {
        return uploaddate;
    }

    public void setuploaddate(LocalDateTime uploaddate) {
        this.uploaddate = uploaddate;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public int getpostid1() {
        return postid1;
    }

    public void setpostid1(int postid1) {
        this.postid1 = postid1;
    }

    public int getpostid2() {
        return postid2;
    }

    public void setpostid2(int postid2) {
        this.postid2 = postid2;
    }

    public int getpostid3() {
        return postid3;
    }

    public void setpostid3(int postid3) {
        this.postid3 = postid3;
    }

    public int getpostid4() {
        return postid4;
    }

    public void setpostid4(int postid4) {
        this.postid4 = postid4;
    }

    public int getpostid5() {
        return postid5;
    }

    public void setpostid5(int postid5) {
        this.postid5 = postid5;
    }
}
