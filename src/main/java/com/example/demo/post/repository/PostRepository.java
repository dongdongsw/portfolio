package com.example.demo.post.repository;

import com.example.demo.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 🔹 특정 유저가 작성한 모든 게시글 조회
    List<Post> findByLogin_id(String loginId);

    // 🔹 닉네임으로 작성한 게시글 검색
    List<Post> findByNickname(String nickname);

    // 🔹 제목에 특정 단어가 포함된 게시글 검색
    List<Post> findByTitleContaining(String keyword);

    // 🔹 제목 또는 내용으로 검색 (검색 기능용)
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

    // 🔹 특정 사용자의 게시글 최신순 정렬
    List<Post> findByLogin_idOrderByUpload_dateDesc(String loginId);

    // 🔹 인기글 (조회수 기준 상위 5개)
    List<Post> findTop5ByOrderByView_countDesc();
}
