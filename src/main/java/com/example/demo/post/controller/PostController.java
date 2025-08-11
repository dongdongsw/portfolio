
package com.example.demo.post.controller;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 임포트
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 추가

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    // 생성: POST /api/posts/post_create (multipart)
    @PostMapping(path = "/post_create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postCreate(@ModelAttribute PostRequestDto dto) {
        postService.createPost(dto);
        return ResponseEntity.ok("작성 완료");
    }

    // 목록: GET /api/posts/post_list
    @GetMapping("/post_list")
    public List<PostResponseDto> postList() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .collect(Collectors.toList());
    }

    // 상세: GET /api/posts/post_detail/{id}
    @GetMapping("/post_detail/{id}")
    public PostResponseDto postDetail(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 수정: POST /api/posts/post_update/{id} (multipart)
    @PostMapping(path = "/post_update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postUpdate(@PathVariable Long id, @ModelAttribute PostRequestDto dto) {
        postService.updatePost(id, dto);
        return ResponseEntity.ok("수정 완료");
    }

    // 삭제: POST /api/posts/post_delete/{id}
    @PostMapping("/post_delete/{id}")
    public ResponseEntity<String> postDelete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("삭제 완료");
    }
}