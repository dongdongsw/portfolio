package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.entity.ImageEntity;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    public PostEntity getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setViewcount(post.getViewcount() + 1);
        return postRepository.save(post);
    }

    public PostEntity createPost(PostRequestDto dto) {
        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setViewcount(0);
        post.setUploaddate(LocalDateTime.now());
        post.setModifydate(LocalDateTime.now());

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagepath0(dto.getImagepath0());
        imageEntity.setImagepath1(dto.getImagepath1());
        imageEntity.setImagepath2(dto.getImagepath2());
        imageEntity.setImagepath3(dto.getImagepath3());
        imageEntity.setImagepath4(dto.getImagepath4());

        post.setImageEntity(imageEntity); // 연관관계 설정됨
        return postRepository.save(post);
    }

    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setModifydate(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

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

        ImageEntity image = post.getImageEntity();
        if (image != null) {
            dto.setImagepath0(image.getImagepath0());
            dto.setImagepath1(image.getImagepath1());
            dto.setImagepath2(image.getImagepath2());
            dto.setImagepath3(image.getImagepath3());
            dto.setImagepath4(image.getImagepath4());
        }
        return dto;
    }
}
