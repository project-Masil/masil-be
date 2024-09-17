package com.masil.backend.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.masil.backend.dto.request.MasilPasswordChangeRequest;
import com.masil.backend.dto.request.MasilProfileUpdateRequest;
import com.masil.backend.dto.response.MasilProfileUpdateResponse;
import com.masil.backend.dto.response.MasilUserResponse;
import com.masil.backend.entity.MasilLikePost;
import com.masil.backend.entity.MasilMember;
import com.masil.backend.entity.MasilProfileStatus;
import com.masil.backend.repository.MasilLikePostRepository;
import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.repository.MasilProfileStatusRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class MasilUserDetailService {

	private final MasilMemberRepository memberRepository;
    private final MasilProfileStatusRepository profileStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final MasilLikePostRepository likePostRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public MasilUserResponse getUserDetail(String nickName) {
        // 유저 정보 조회
        MasilMember member = memberRepository.findByUserEmail(nickName)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 프로필 정보 조회
        MasilProfileStatus profileStatus = profileStatusRepository.findById(member.getUserEmail())
            .orElseThrow(() -> new RuntimeException("프로필 정보를 찾을 수 없습니다."));

        // 응답 데이터 생성
        return MasilUserResponse.builder()
            .userId(member.getUserId())
            .profileImage(profileStatus.getProfileImgName()) // 프로필 이미지 파일 이름
            .profileImagePath(profileStatus.getProfileImgPath())	// 프로필 이미지 경로
            .nickName(member.getUserId())
            .statusMessage(profileStatus.getProfileMsg()) // 상태 메시지
            .build();
    }

    public void passWordChange(MasilPasswordChangeRequest passWordChange) {
        // 현재 유저 인증 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        MasilMember member = memberRepository.findByUserEmail(currentUserName)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + currentUserName));

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(passWordChange.getOldPassword(), member.getUserPwd())) {
            throw new RuntimeException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 설정
        member.setUserPwd(passwordEncoder.encode(passWordChange.getNewPassword()));
        memberRepository.save(member);
    }

    public MasilProfileUpdateResponse updateUserProfile(MasilProfileUpdateRequest request) {
        MasilProfileStatus profileStatus = profileStatusRepository.findById(request.getUser_email())
            .orElseThrow(() -> new RuntimeException("프로필 정보를 찾을 수 없습니다."));

        // 프로필 이미지 업데이트
        if (profileStatus.getProfileReImgName() != null) {
            deleteImageFromS3(profileStatus.getProfileReImgName());
        }

        // 새 이미지 업로드 및 경로 설정
        String newFileName = uploadImageToS3(request.getProfileImage());

        profileStatus.setProfileImgName(request.getProfileImage().getOriginalFilename());
        profileStatus.setProfileReImgName(newFileName);
        profileStatus.setProfileImgPath("https://s3.amazonaws.com/" + bucketName + "/" + newFileName);
        profileStatus.setProfileSize(request.getProfileImage().getSize());

        // 상태 메시지 업데이트
        profileStatus.setProfileMsg(request.getStatusMessage());

        profileStatusRepository.save(profileStatus);

        MasilMember member = memberRepository.findByUserEmail(request.getUser_email())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return MasilProfileUpdateResponse.builder()
            .profileImageUrl(profileStatus.getProfileImgPath())
            .nickName(member.getUserId())
            .statusMessage(profileStatus.getProfileMsg())
            .build();
    }

    // S3에서 이미지 삭제 메서드
    private void deleteImageFromS3(String imageName) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(imageName)
            .build();
        s3Client.deleteObject(deleteRequest);
    }

    // S3에 이미지 업로드 메서드
    private String uploadImageToS3(MultipartFile image) {
        String uniqueFileName = "masil_" + UUID.randomUUID().toString() + ".png";

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(image.getContentType())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("S3에 이미지를 업로드하는 중 오류가 발생했습니다.", e);
        }

        return uniqueFileName;
    }

    // 찜하기 OR 좋아요 추가
    // 찜하기 상태 확인 메서드
    public boolean isPostBookmarkedByUser(String userEmail, int postId) {
        MasilLikePost likePost = likePostRepository.findById(userEmail)
                .orElse(new MasilLikePost());

        return likePost.getLikePost() == postId;
    }

    // 좋아요 상태 확인 메서드
    public boolean isPostLikedByUser(String userEmail, int postId) {
        MasilLikePost likePost = likePostRepository.findById(userEmail)
                .orElse(new MasilLikePost());

        return likePost.getGreatPost() == postId;
    }

    // 게시글 찜하기
    public void likePost(String userEmail, int postId) {
        MasilLikePost likePost = likePostRepository.findById(userEmail)
                .orElse(MasilLikePost.builder().userEmail(userEmail).build());

        likePost.setLikePost(postId);  // 찜한 게시글 번호 업데이트
        likePost.setGreatPost(0);   // 좋아요는 0으로 설정

        likePostRepository.save(likePost);
    }

    // 게시글 좋아요
    public void greatPost(String userEmail, int postId) {
        MasilLikePost likePost = likePostRepository.findById(userEmail)
                .orElse(MasilLikePost.builder().userEmail(userEmail).build());

        likePost.setGreatPost(postId);  // 좋아요한 게시글 번호 업데이트
        likePost.setLikePost(0);     // 찜은 0으로 설정

        likePostRepository.save(likePost);
    }

    // 찜/좋아요 취소
    public void cancelLikeOrGreatPost(String userEmail, boolean isLike) {
        MasilLikePost likePost = likePostRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));

        if (isLike) {
            likePost.setLikePost(0);  // 찜 취소
        } else {
            likePost.setGreatPost(0);  // 좋아요 취소
        }

        likePostRepository.save(likePost);
    }

    // 찜한 게시글 조회
    public List<Integer> getLikedPosts(String userEmail) {
        List<MasilLikePost> likedPosts = likePostRepository.findByUserEmailAndLikePostNotNull(userEmail);
        return likedPosts.stream().map(MasilLikePost::getLikePost).collect(Collectors.toList());
    }

    // 좋아요한 게시글 조회
    public List<Integer> getGreatPosts(String userEmail) {
        List<MasilLikePost> greatPosts = likePostRepository.findByUserEmailAndGreatPostNotNull(userEmail);
        return greatPosts.stream().map(MasilLikePost::getGreatPost).collect(Collectors.toList());
    }

    // 찜하기와 좋아요 모두 조회
    public Map<String, List<Integer>> getAllLikedAndGreatPosts(String userEmail) {
        List<MasilLikePost> likedPosts = likePostRepository.findByUserEmailAndLikePostNotNull(userEmail);
        List<MasilLikePost> greatPosts = likePostRepository.findByUserEmailAndGreatPostNotNull(userEmail);

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("likedPosts", likedPosts.stream().map(MasilLikePost::getLikePost).collect(Collectors.toList()));
        result.put("greatPosts", greatPosts.stream().map(MasilLikePost::getGreatPost).collect(Collectors.toList()));

        return result;
    }
}
