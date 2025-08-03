package com.example.demo.Comment.Repository;

import com.example.demo.Comment.Entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    // 특정 게시글의 모든 댓글 조회
    List<CommentEntity> findByPostId(int postId);

    // 특정 게시글의 댓글을 업로드 시간 순으로 정렬하여 조회
    List<CommentEntity> findByPostIdOrderByUploadDateAsc(int postId);

    // 특정 게시글의 댓글을 업로드 시간 역순으로 정렬하여 조회
    List<CommentEntity> findByPostIdOrderByUploadDateDesc(int postId);

    // 특정 사용자(로그인 ID)가 작성한 모든 댓글 조회
    List<CommentEntity> findByLoginId(String loginId);

    // 특정 닉네임으로 작성된 모든 댓글 조회
    List<CommentEntity> findByNickname(String nickname);

    // 특정 작성자의 모든 댓글 조회
    List<CommentEntity> findByAuthor(String author);

    // 특정 게시글에서 특정 사용자가 작성한 댓글 조회
    List<CommentEntity> findByPostIdAndLoginId(int postId, String loginId);

    // 특정 게시글의 댓글 개수 조회
    long countByPostId(int postId);

    // 특정 사용자가 작성한 댓글 개수 조회
    long countByLoginId(String loginId);

    // 특정 기간 내에 작성된 댓글 조회
    List<CommentEntity> findByUploadDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 댓글 내용으로 검색 (부분 일치)
    List<CommentEntity> findByContentContaining(String keyword);

    // 특정 게시글에서 댓글 내용으로 검색
    List<CommentEntity> findByPostIdAndContentContaining(int postId, String keyword);

    // 특정 게시글의 최신 댓글부터 페이징 처리하여 조회
    @Query("SELECT c FROM CommentEntity c WHERE c.postId = :postId ORDER BY c.uploadDate DESC")
    List<CommentEntity> findRecentCommentsByPostId(@Param("postId") int postId);

    // 특정 사용자의 최근 댓글 조회 (상위 10개)
    List<CommentEntity> findTop10ByLoginIdOrderByUploadDateDesc(String loginId);

    // 특정 게시글에서 특정 댓글 ID와 게시글 ID로 댓글 존재 여부 확인
    boolean existsByIdAndPostId(int id, int postId);

    // 특정 게시글에서 특정 사용자의 댓글 존재 여부 확인
    boolean existsByPostIdAndLoginId(int postId, String loginId);

    // 특정 게시글의 댓글들을 일괄 삭제
    void deleteByPostId(int postId);

    // 특정 사용자의 모든 댓글 삭제
    void deleteByLoginId(String loginId);
}