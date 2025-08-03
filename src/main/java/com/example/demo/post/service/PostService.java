package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 1. 게시글 전체 조회
    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    // 2. 게시글 상세 조회 + 조회수 증가
    public PostEntity getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setViewcount(post.getViewcount() + 1);
        return postRepository.save(post);
    }

    // 3. 게시글 작성
    public PostEntity createPost(PostRequestDto dto) {
        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setImagepath0(String.valueOf(dto.getImagepath0()));
        post.setImagepath1(dto.getImagepath1());
        post.setImagepath2(dto.getImagepath2());
        post.setImagepath3(dto.getImagepath3());
        post.setImagepath4(dto.getImagepath4());
        post.setViewcount(0);
        post.setUploaddate(LocalDateTime.now());
        post.setModifydate(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 4. 게시글 수정
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setModifydate(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 5. 게시글 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // 6. Post → PostResponseDto 변환
    public PostResponseDto toResponseDto(PostEntity post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setLoginid(post.getLoginid());
        dto.setNickname(post.getNickname());
        dto.setContent(post.getContent());
        dto.setModifydate(post.getModifydate());
        dto.setUploaddate(post.getUploaddate());
        dto.setViewcount(post.getViewcount());
        dto.setTitle(post.getTitle());
        dto.setImagepath0(post.getImagepath0());
        dto.setImagepath1(post.getImagepath1());
        dto.setImagepath2(post.getImagepath2());
        dto.setImagepath3(post.getImagepath3());
        dto.setImagepath4(post.getImagepath4());
        return dto;
    }
}
