package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.entity.ImageEntity;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/images/";


    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    public PostEntity getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setViewcount(post.getViewcount() + 1);
        return postRepository.save(post);
    }

    @Transactional
    public PostEntity createPost(PostRequestDto dto) {
        // 파일을 저장할 디렉토리가 없으면 생성합니다.
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setViewcount(0);

        ImageEntity imageEntity = new ImageEntity();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (int i = 0; i < dto.getFiles().size(); i++) {
                MultipartFile file = dto.getFiles().get(i);
                if (!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String savedFileName = UUID.randomUUID().toString() + extension;
                        File targetFile = new File(directory, savedFileName);

                        file.transferTo(targetFile);
                        String webAccessiblePath = "/images/" + savedFileName;

                        if (i == 0) imageEntity.setImagepath0(webAccessiblePath);
                        else if (i == 1) imageEntity.setImagepath1(webAccessiblePath);
                        else if (i == 2) imageEntity.setImagepath2(webAccessiblePath);
                        else if (i == 3) imageEntity.setImagepath3(webAccessiblePath);
                        else if (i == 4) imageEntity.setImagepath4(webAccessiblePath);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }

        post.setImageEntity(imageEntity);
        return postRepository.save(post);
    }

    @Transactional
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            ImageEntity existingImage = post.getImageEntity();

            ImageEntity image = (existingImage != null) ? existingImage : new ImageEntity();
            if (existingImage == null) {
                post.setImageEntity(image);
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int i = 0; i < dto.getFiles().size(); i++) {
                MultipartFile file = dto.getFiles().get(i);
                if (!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String savedFileName = UUID.randomUUID().toString() + extension;
                        File targetFile = new File(directory, savedFileName);

                        file.transferTo(targetFile);

                        String webAccessiblePath = "/images/" + savedFileName;

                        if (i == 0) image.setImagepath0(webAccessiblePath);
                        else if (i == 1) image.setImagepath1(webAccessiblePath);
                        else if (i == 2) image.setImagepath2(webAccessiblePath);
                        else if (i == 3) image.setImagepath3(webAccessiblePath);
                        else if (i == 4) image.setImagepath4(webAccessiblePath);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        ImageEntity imageEntity = post.getImageEntity();
        if (imageEntity != null) {
            String[] paths = {
                    imageEntity.getImagepath0(), imageEntity.getImagepath1(), imageEntity.getImagepath2(),
                    imageEntity.getImagepath3(), imageEntity.getImagepath4()
            };
            for (String path : paths) {
                if (path != null && path.startsWith("/images/")) {
                    File fileToDelete = new File(uploadDir, path.substring("/images/".length()));
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                }
            }
        }
        postRepository.delete(post); // 데이터베이스에서 게시글 삭제
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