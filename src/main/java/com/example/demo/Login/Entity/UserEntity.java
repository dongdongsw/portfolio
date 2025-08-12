package com.example.demo.Login.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class) //@LastModifiedDate, @CreatedDate를 쓰기 위해서 추가 해야함
public class UserEntity {

    // Getters and Setters


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 회원 고유번호

    @Column(name = "login_id",unique = true, length = 25, nullable = false, updatable = false)
    private String loginid;  // 아이디

    @Column(name = "login_pw", length = 255, nullable = false) //unique는 사용하지 않음 Security를 사용해서 암호화 하기
    private String loginpw;  // 비밀번호

    @Column(unique = true, length = 20, nullable = false)
    private String nickname;  // 닉네임

    @Column (nullable = false, unique = true, updatable = false)
    private String email;  // 이메일

    @Column (nullable = false)
    private String role;  // 권한

    @CreatedDate
    @Column(name = "regist_date", nullable = false)
    private LocalDateTime registdate;  // 회원가입 날짜

    @LastModifiedDate
    private LocalDateTime pwupdate;  // 비밀번호 재설정 날짜



    public int getId() { return id; }

    public void setId(int id) { this.id = id;}

    public String getLoginid() { return loginid; }

    public void setLoginid(String login_id) { this.loginid = login_id; }

    public String getUserPwd() { return loginpw; }

    public void setUserPwd(String login_pw) { this.loginpw = login_pw; }

    public String getNickName() { return nickname; }

    public void setNickName(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public LocalDateTime getPwudate() { return pwupdate; }

    public void setPwupdate(LocalDateTime pwupdate) { this.pwupdate = pwupdate; }

    public LocalDateTime getRegistDate() { return registdate; }

    public void setRegistDate(LocalDateTime regist_date) { this.registdate = regist_date; }
}
