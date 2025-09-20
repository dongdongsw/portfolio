package com.example.demo.post.repository;

import com.example.demo.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {


    List<PostEntity> findByIdIn(List<Long> ids);

    // 최신글 3개 불러오기
    List<PostEntity> findTop3ByOrderByUploaddateDesc();
}