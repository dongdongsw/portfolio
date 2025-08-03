package com.example.demo.post.repository;

import com.example.demo.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // 최근 본 게시글 ID들로 조회
    List<PostEntity> findByIdIn(List<Long> ids);
}