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

    // 작성자(loginId) 별 댓글 목록 조회
    List<CommentEntity> findByLoginId(String loginId);

    // 게시글(postId) 기준, 업로드 순 최신순 조회
    List<CommentEntity> findByPostIdOrderByUploadDateDesc(int postId);

    // 수정일 기준 최신순
    List<CommentEntity> findByPostIdOrderByModifyDateDesc(int postId);
    // 필요하다면 추가적으로 원하는 쿼리 직접 메서드 생성 가능

    Page<CommentEntity> findByPostId(int postId, Pageable pageable);
}