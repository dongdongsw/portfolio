package com.example.demo.post.controller;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1. 글 작성
    @PostMapping
    public String createPost(@RequestBody PostRequestDto dto) {
        postService.createPost(dto);
        return "작성 완료";
    }

    // 2. 글 전체 조회
    @GetMapping
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3. 글 상세 조회
    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 4. 글 수정
    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        postService.updatePost(id, dto);
        return "수정 완료";
    }

    // 5. 글 삭제
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "삭제 완료";
    }
}
