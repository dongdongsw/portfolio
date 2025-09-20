package com.example.demo.Comment.Repository;

import com.example.demo.Comment.Entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    // 게시글(postId)로 댓글 목록 조회
    List<CommentEntity> findByPostId(int postId);

    // 게시글(postId) 기준, 수정일 기준 최신순 조회
    List<CommentEntity> findByPostIdOrderByModifyDateDesc(int postId);

    // 페이징 처리
    Page<CommentEntity> findByPostId(int postId, Pageable pageable);
}