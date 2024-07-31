package com.masil.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.masil.backend.dto.request.MasilPasswordChangeRequest;
import com.masil.backend.dto.request.MasilProfileUpdateRequest;
import com.masil.backend.dto.response.MasilProfileUpdateResponse;
import com.masil.backend.dto.response.MasilUserResponse;
import com.masil.backend.entity.MasilMember;
import com.masil.backend.entity.MasilProfileStatus;
import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.repository.MasilProfileStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasilUserDetailService {

	private final MasilMemberRepository memberRepository;
    private final MasilProfileStatusRepository profileStatusRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${profile.image.directory}")	// 프로필 이미지 파일 저장 경로
    private String profileImageDirectory;

    public MasilUserResponse getUserDetail(String nickName) {
        // 유저 정보 조회
        MasilMember member = memberRepository.findByUserId(nickName)
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
        if (!request.getProfileImage().isEmpty()) {
            // 기존 이미지 파일 삭제
            if (profileStatus.getProfileImgName() != null) {
                Path oldImagePath = Paths.get(profileImageDirectory, profileStatus.getProfileImgName());
                try {
                    Files.deleteIfExists(oldImagePath);
                } catch (IOException e) {
                    throw new RuntimeException("기존 프로필 이미지 파일 삭제에 실패했습니다.", e);
                }
            }

            // 새 이미지 파일 저장
            String newFileName = request.getProfileImage().getOriginalFilename();
            Path newImagePath = Paths.get(profileImageDirectory, newFileName);
            try {
                Files.copy(request.getProfileImage().getInputStream(), newImagePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("새 프로필 이미지 파일 저장에 실패했습니다.", e);
            }

            profileStatus.setProfileImgName(newFileName);
            profileStatus.setProfileImgPath(newImagePath.toString());
            profileStatus.setProfileSize(request.getProfileImage().getSize());
        }

        // 상태 메시지 업데이트
        profileStatus.setProfileMsg(request.getStatusMessage());

        profileStatusRepository.save(profileStatus);

        return MasilProfileUpdateResponse.builder()
            .profileImageUrl(profileStatus.getProfileImgPath())
            .nickName(profileStatus.getUserEmail())
            .statusMessage(profileStatus.getProfileMsg())
            .build();
    }
}
