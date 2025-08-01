package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.Post;
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
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 2. 게시글 상세 조회 + 조회수 증가
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setviewcount(post.getviewcount() + 1);
        return postRepository.save(post);
    }

    // 3. 게시글 작성
    public Post createPost(PostRequestDto dto) {
        Post post = new Post();
        post.setloginid(dto.getLoginid());
        post.setnickname(dto.getNickname());
        post.setcontent(dto.getContent());
        post.settitle(dto.getTitle());
        post.setimagepath0(dto.getImagepath0());
        post.setimagepath1(dto.getImagepath1());
        post.setimagepath2(dto.getImagepath2());
        post.setimagepath3(dto.getImagepath3());
        post.setimagepath4(dto.getImagepath4());
        post.setpostid1(dto.getPostid1());
        post.setpostid2(dto.getPostid2());
        post.setpostid3(dto.getPostid3());
        post.setpostid4(dto.getPostid4());
        post.setpostid5(dto.getPostid5());
        post.setviewcount(0);
        post.setuploaddate(LocalDateTime.now());
        post.setmodifydate(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 4. 게시글 수정
    public Post updatePost(Long id, PostRequestDto dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        post.settitle(dto.getTitle());
        post.setcontent(dto.getContent());
        post.setmodifydate(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 5. 게시글 삭제
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // 6. 최근 본 게시글 5개 조회
    public List<Post> getRecentPosts(Post post) {
        List<Long> ids = List.of(
                (long) post.getpostid1(),
                (long) post.getpostid2(),
                (long) post.getpostid3(),
                (long) post.getpostid4(),
                (long) post.getpostid5()
        );
        return postRepository.findByIdIn(ids);
    }

    // 7. Post → PostResponseDto 변환
    public PostResponseDto toResponseDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getid());
        dto.setLoginid(post.getloginid());
        dto.setNickname(post.getnickname());
        dto.setContent(post.getcontent());
        dto.setModifydate(post.getmodifydate());
        dto.setUploaddate(post.getuploaddate());
        dto.setViewcount(post.getviewcount());
        dto.setTitle(post.gettitle());
        dto.setImagepath0(post.getimagepath0());
        dto.setImagepath1(post.getimagepath1());
        dto.setImagepath2(post.getimagepath2());
        dto.setImagepath3(post.getimagepath3());
        dto.setImagepath4(post.getimagepath4());
        dto.setPostid1(post.getpostid1());
        dto.setPostid2(post.getpostid2());
        dto.setPostid3(post.getpostid3());
        dto.setPostid4(post.getpostid4());
        dto.setPostid5(post.getpostid5());
        return dto;
    }
}
