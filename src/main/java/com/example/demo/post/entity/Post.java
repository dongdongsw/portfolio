package com.example.demo.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "post_entity", uniqueConstraints = @UniqueConstraint(columnNames = {"externalId", "alias"}))
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String externalId;

    //private String id;
    private String login_id;
    private String nickname;
    private String content;
    private LocalDateTime modify_date;
    private int view_count;
    private LocalDateTime upload_date;
    private String title;
    private String image_path0;
    private String image_path1;
    private String image_path2;
    private String image_path3;
    private String image_path4;
    @Column(nullable = false)
    private String alias;



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLogin_id() {return login_id;}

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content;}

    public LocalDateTime getModify_date() { return modify_date; }

    public void setModify_date(LocalDateTime modify_date) { this.modify_date = modify_date; }


    public int getView_count() { return view_count; }


    public void setView_count(int view_count) { this.view_count = view_count; }


    public LocalDateTime getUpload_date() { return upload_date; }


    public void setUpload_date(LocalDateTime upload_date) { this.upload_date = upload_date; }


    public String getTitle() { return title; }


    public void setTitle(String title) { this.title = title; }


    public String getAlias() { return alias; }


    public String getImage_path0() { return image_path0; }


    public void setImage_path0(String image_path0) { this.image_path0 = image_path0;}

    public String getImage_path1() { return image_path1; }

    public void setImage_path1(String image_path1) { this.image_path1 = image_path1;}

    public String getImage_path2() { return image_path2; }

    public void setImage_path2(String image_path2) { this.image_path2 = image_path2;}

    public String getImage_path3() { return image_path3; }

    public void setImage_path3(String image_path3) { this.image_path3 = image_path3;}

    public String getImage_path4() { return image_path4; }

    public void setImage_path4(String image_path4) { this.image_path4 = image_path4;}

    public void setAlias(String alias) { this.alias = alias; }



    // 전체 주소 가공
    /*public void constructFullAddress() {
        if (detailAddress != null && !detailAddress.isEmpty()) {
            this.fullAddress = address + " " + detailAddress;
        } else {
            this.fullAddress = address;
        }
    }*/
}
