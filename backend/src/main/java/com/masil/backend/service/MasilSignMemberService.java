package com.masil.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.masil.backend.dto.request.MasilMemberCheckRequest;
import com.masil.backend.entity.MasilMember;
import com.masil.backend.entity.MasilProfileStatus;
import com.masil.backend.repository.MasilMemberLoginTryCountRepository;
import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.repository.MasilProfileStatusRepository;
import com.masil.backend.util.Jwt.JwtUtil;
import com.masil.backend.util.Redis.RedisUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasilSignMemberService {

    private final MasilMemberRepository masilMemberRepository;
    private final MasilProfileStatusRepository profileStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final MasilMemberLoginTryCountRepository loginTryCountRepository;

    @Value("${profile.image.directory}")	// 프로필 이미지 파일 저장 경로
    private String profileImageDirectory;

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
            String profileImgName = memberCheckDto.getProfile().getOriginalFilename();
            String profileReImgName = saveProfileImage(memberCheckDto.getProfile());
            Long profileSize = memberCheckDto.getProfile().getSize();

            MasilProfileStatus profileStatus = MasilProfileStatus.builder()
                    .userEmail(memberCheckDto.getEmail())
                    .profileImgName(profileImgName)
                    .profileReImgName(profileReImgName)
                    .profileSize(profileSize)
                    .profileMsg(memberCheckDto.getStatusMessage())
                    .profileImgPath(profileImageDirectory + profileReImgName)
                    .build();

            profileStatusRepository.save(profileStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile image", e);
        }
    }

    private String saveProfileImage(MultipartFile profileImg) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + profileImg.getOriginalFilename();
        Path destinationPath = Paths.get(profileImageDirectory, uniqueFileName);
        Files.createDirectories(destinationPath.getParent()); // 디렉토리 생성
        profileImg.transferTo(destinationPath); // 파일 저장
        return uniqueFileName;
    }

    private void deleteProfileImage(String profileImgPath) throws IOException {
        Path path = Paths.get(profileImgPath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    @Transactional
    public void resignMember(String email, String token) {
    	// 프로필 정보 조회
        MasilProfileStatus profileStatus = profileStatusRepository.findById(email)
            .orElseThrow(() -> new RuntimeException("프로필 정보를 찾을 수 없습니다."));

        // 프로필 이미지 삭제
        try {
            deleteProfileImage(profileStatus.getProfileImgPath());
        } catch (IOException e) {
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


}