package com.example.demo.Comment.Service;

import com.example.demo.Comment.dto.CommentRequestDto;
import com.example.demo.Comment.dto.CommentResponseDto;
import com.example.demo.Comment.Entity.CommentEntity;
import com.example.demo.Comment.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 댓글 등록
    @Transactional
    public CommentResponseDto createCommentForPost(int postId, CommentRequestDto requestDto) {
        CommentEntity comment = new CommentEntity();
        comment.setPostId(postId);
        comment.setLoginId(requestDto.getLoginId());
        comment.setNickname(requestDto.getNickname());
        comment.setContent(requestDto.getContent());

        CommentEntity saved = commentRepository.save(comment);
        return toResponseDto(saved);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(int commentId, CommentRequestDto requestDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        comment.setContent(requestDto.getContent());
        comment.setNickname(requestDto.getNickname());
        CommentEntity updated = commentRepository.save(comment);

        return toResponseDto(updated);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다");
        }
        commentRepository.deleteById(commentId);
    }

    // 특정 게시글의 전체 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(int postId) {
        List<CommentEntity> comments = commentRepository.findByPostIdOrderByModifyDateDesc(postId);
        return comments.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    // Entity → ResponseDto 변환
    private CommentResponseDto toResponseDto(CommentEntity entity) {
        return new CommentResponseDto(
                entity.getId(),
                entity.getPostId(),
                entity.getLoginId(),
                entity.getNickname(),
                entity.getContent(),
                entity.getUploadDate(),
                entity.getModifyDate()
        );
    }
}