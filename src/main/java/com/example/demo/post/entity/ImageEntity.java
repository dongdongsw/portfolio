package com.example.demo.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "image_entity")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public PostEntity getPost() { return post; }
    public void setPost(PostEntity post) { this.post = post; }
}
