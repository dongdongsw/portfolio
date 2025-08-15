package com.example.demo.MyPage.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "RecentView")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class RecentViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id",  nullable = false)
    private String postid;

    @Column(name = "login_id", nullable = false)
    private String loginid;

    @CreatedDate
    @Column(name = "view_date", nullable = false, updatable = false)
    private LocalDateTime viewdate;

}
