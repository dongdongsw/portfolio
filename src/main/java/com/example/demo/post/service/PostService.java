/*
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
*/

/*
package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.entity.ImageEntity;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 추가
import org.springframework.web.multipart.MultipartFile; // 추가

import java.io.File; // 파일 처리
import java.io.IOException; // IO 예외 처리
import java.time.LocalDateTime; // LocalDateTime 사용을 위해 필요 (Auditing이 동작하면 일부 불필요할 수 있음)
import java.util.List;
import java.util.UUID; // 고유한 파일명 생성을 위함

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final String uploadPath = "C:/tmp/images/"; // 실제 환경에 맞게 변경하세요!

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    public PostEntity getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setViewcount(post.getViewcount() + 1);
        return postRepository.save(post);
    }

    @Transactional // 트랜잭션 처리
    public PostEntity createPost(PostRequestDto dto) {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 디렉토리가 없으면 생성
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
                        String filePath = uploadPath + savedFileName; // 저장될 최종 경로

                        file.transferTo(new File(filePath));

                        if (i == 0) imageEntity.setImagepath0(filePath);
                        else if (i == 1) imageEntity.setImagepath1(filePath);
                        else if (i == 2) imageEntity.setImagepath2(filePath);
                        else if (i == 3) imageEntity.setImagepath3(filePath);
                        else if (i == 4) imageEntity.setImagepath4(filePath);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }

        post.setImageEntity(imageEntity); // 연관관계 설정됨
        return postRepository.save(post);
    }

    @Transactional // 트랜잭션 처리
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        // @LastModifiedDate 가 Auditing을 통해 자동으로 설정되므로,
        // 이 부분은 주석 처리하거나 삭제하는 것이 더 좋습니다.
        // post.setModifydate(LocalDateTime.now());

        // 이미지 수정 로직 (새로운 파일 업로드 또는 기존 파일 교체)
        // 이 부분은 기존 ImageEntity를 가져와서,
        // 새로운 파일이 있다면 기존 경로를 업데이트하거나 삭제 후 새로 저장하는 로직이 필요합니다.
        // 여기서는 예시로 생략하지만, 실제 구현에서는 고려해야 합니다.

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
*/
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

import java.io.File; // 파일 처리를 위해 필요
import java.io.IOException; // IO 예외 처리를 위해 필요
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID; // 고유한 파일명 생성을 위해 필요


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // --- 핵심 수정 1: 이미지 파일이 저장될 서버의 실제 디렉토리 경로 ---
    // 이 경로는 PostService, WebConfig에서 동일하게 사용되어야 합니다.
    private final String uploadDir = "C:/tmp/images/";
    // --- 수정 끝 ---


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
        // 파일 저장 디렉토리 생성 로직
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // 디렉토리가 없으면 생성
        }

        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setViewcount(0);
        // Auditing을 사용한다면 setUploaddate, setModifydate는 주석 처리하는 것이 좋습니다.
        // post.setUploaddate(LocalDateTime.now());
        // post.setModifydate(LocalDateTime.now());

        ImageEntity imageEntity = new ImageEntity();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (int i = 0; i < dto.getFiles().size(); i++) {
                MultipartFile file = dto.getFiles().get(i);
                if (!file.isEmpty()) {
                    try {
                        // --- 핵심 수정 2: 파일 저장 및 웹 접근 가능한 경로(URL) 저장 ---
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String savedFileName = UUID.randomUUID().toString() + extension; // 고유한 파일명
                        File targetFile = new File(uploadDir, savedFileName); // 실제 디스크에 저장될 파일 경로

                        file.transferTo(targetFile); // 파일을 실제 디스크에 저장

                        // WebConfig에서 /images/** 로 매핑된 경로를 사용합니다.
                        // 이 경로를 HTML의 img src에 직접 사용하면 웹 서버를 통해 파일이 제공됩니다.
                        String webAccessiblePath = "/images/" + savedFileName; // 예를 들어 /images/abc.png

                        if (i == 0) imageEntity.setImagepath0(webAccessiblePath);
                        else if (i == 1) imageEntity.setImagepath1(webAccessiblePath);
                        else if (i == 2) imageEntity.setImagepath2(webAccessiblePath);
                        else if (i == 3) imageEntity.setImagepath3(webAccessiblePath);
                        else if (i == 4) imageEntity.setImagepath4(webAccessiblePath);
                        // --- 수정 끝 ---

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }

        post.setImageEntity(imageEntity); // 연관관계 설정됨
        return postRepository.save(post);
    }

    @Transactional
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        // Auditing을 사용한다면 setModifydate는 주석 처리하는 것이 좋습니다.
        // post.setModifydate(LocalDateTime.now());

        // 이미지 수정 로직 (파일 저장 방식으로 변경)
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // 기존 이미지 삭제 로직 (선택 사항)
            ImageEntity existingImage = post.getImageEntity();
            // 여기서는 기존 파일 삭제 로직은 생략합니다. 실제 구현에서는 고려해주세요.

            ImageEntity image = (existingImage != null) ? existingImage : new ImageEntity();
            if (existingImage == null) {
                post.setImageEntity(image);
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
                        File targetFile = new File(uploadDir, savedFileName);

                        file.transferTo(targetFile);

                        String webAccessiblePath = "/images/" + savedFileName; // /images/abc.png

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
        // 게시글 삭제 시 연관된 이미지 파일도 삭제
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        ImageEntity imageEntity = post.getImageEntity();
        if (imageEntity != null) {
            String[] paths = {
                    imageEntity.getImagepath0(), imageEntity.getImagepath1(), imageEntity.getImagepath2(),
                    imageEntity.getImagepath3(), imageEntity.getImagepath4()
            };
            for (String path : paths) {
                if (path != null && path.startsWith("/images/")) { // /images/ 경로로 시작하는 파일만 삭제 시도
                    File fileToDelete = new File(uploadDir, path.substring("/images/".length()));
                    if (fileToDelete.exists()) {
                        fileToDelete.delete(); // 실제 파일 삭제
                    }
                }
            }
        }
        postRepository.delete(post); // 엔티티를 사용하여 삭제
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
            // 핵심: ImageEntity에 저장된 웹 접근 가능한 경로(URL)를 그대로 반환합니다.
            dto.setImagepath0(image.getImagepath0());
            dto.setImagepath1(image.getImagepath1());
            dto.setImagepath2(image.getImagepath2());
            dto.setImagepath3(image.getImagepath3());
            dto.setImagepath4(image.getImagepath4());
        }
        return dto;
    }
}