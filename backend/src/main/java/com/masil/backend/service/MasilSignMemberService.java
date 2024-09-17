package com.masil.backend.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.masil.backend.dto.request.MasilMemberCheckRequest;
import com.masil.backend.entity.MasilMember;
import com.masil.backend.entity.MasilProfileStatus;
import com.masil.backend.repository.MasilMemberLoginTryCountRepository;
import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.repository.MasilProfileStatusRepository;
import com.masil.backend.util.Jwt.JwtUtil;
import com.masil.backend.util.Redis.RedisUtil;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
@RequiredArgsConstructor
public class MasilSignMemberService {

    private final MasilMemberRepository masilMemberRepository;
    private final MasilProfileStatusRepository profileStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final MasilMemberLoginTryCountRepository loginTryCountRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-name}") // S3 버킷 이름 주입
    private String bucketName;

    public boolean isIdExists(String id) {
        return masilMemberRepository.existsByUserId(id);
    }

    @Transactional
    public void registerMember(MasilMemberCheckRequest memberCheckDto) throws IOException {
        int maxUserNumber = masilMemberRepository.findMaxUserNumber();
        int newUserNumber = maxUserNumber + 1;

        MasilMember member = MasilMember.builder()
                .userEmail(memberCheckDto.getEmail())
                .userNumber(newUserNumber)
                .userId(memberCheckDto.getNickName())
                .userPwd(passwordEncoder.encode(memberCheckDto.getPassword()))
                .userRole(1)
                .signupDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        masilMemberRepository.save(member);

        try {
        	// S3에 프로필 이미지 업로드 후 URL 받기
        	String profileImgUrl = s3Service.uploadFile(memberCheckDto.getProfile());
        	String profileReImgName = profileImgUrl.substring(profileImgUrl.lastIndexOf("/") + 1); // URL에서 파일명 추출
        	Long profileSize = memberCheckDto.getProfile().getSize();

            MasilProfileStatus profileStatus = MasilProfileStatus.builder()
                    .userEmail(memberCheckDto.getEmail())
                    .profileImgName(memberCheckDto.getProfile().getOriginalFilename())
                    .profileReImgName(profileReImgName)
                    .profileSize(profileSize)
                    .profileMsg(memberCheckDto.getStatusMessage())
                    .profileImgPath(profileImgUrl)    // S3 URL 저장
                    .build();

            profileStatusRepository.save(profileStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile image", e);
        }
    }

    @Transactional
    public void resignMember(String email, String token) {
    	// 프로필 정보 조회
        MasilProfileStatus profileStatus = profileStatusRepository.findById(email)
            .orElseThrow(() -> new RuntimeException("프로필 정보를 찾을 수 없습니다."));

        // 프로필 이미지 삭제
        try {
        	 deleteProfileImageFrom(profileStatus.getProfileReImgName());
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 삭제 중 오류가 발생했습니다.", e);
        }

    	// 프로필 정보 삭제
    	profileStatusRepository.deleteById(email);
    	// 로그인시도 정보 삭제
    	loginTryCountRepository.deleteById(email);
        // 유저 정보 삭제
        masilMemberRepository.deleteById(email);

        // JWT 토큰을 블랙리스트에 추가
        jwtUtil.addToBlacklist(token);
    }

    private void deleteProfileImageFrom(String profileReImgName) {
        try {
            // S3에서 파일 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(profileReImgName) // S3에서 삭제할 파일의 키
                .build();
            s3Service.deleteFile(profileReImgName);
        } catch (IOException  e) {
            throw new RuntimeException("S3에서 파일 삭제 중 오류가 발생했습니다.", e);
        }
    }


}