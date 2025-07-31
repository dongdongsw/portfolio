package com.example.demo.Login.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"id","userID","userPwd","nickname","e_mail"}))
public class User {

    // Getters and Setters

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 회원 고유번호

    private String login_id;  // 아이디
    private String login_pw;  // 비밀번호
    private String nickname;  // 닉네임
    private String email;  // 이메일
    private String role;  // 권한
    private int post_id1;
    private int post_id2;
    private int post_id3;
    private int post_id4;
    private int post_id5;

    @CreatedDate
    @Column (updatable = false)
    private LocalDateTime pwudate;  // 비밀번호 재설정 날짜
    private LocalDateTime regist_date;  // 회원가입 날짜

    public int getID() { return id; }

    public void setId(int id) { this.id = id;}

    public String getUserId() { return login_id; }

    public void setUserId(String login_id) { this.login_id = login_id; }

    public String getUserPwd() { return login_pw; }

    public void setUserPwd(String login_pw) { this.login_pw = login_pw; }

    public String getNickName() { return nickname; }

    public void setNickName(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public LocalDateTime getPwudate() { return pwudate; }

    public void setPwudate(LocalDateTime pwdate) { this.pwudate = pwdate; }

    public LocalDateTime getRegistDate() { return regist_date; }

    public void setRegistDate(LocalDateTime regist_date) { this.regist_date = regist_date; }
}
