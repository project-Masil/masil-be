package com.masil.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.masil.backend.dto.request.MasilMemberDetailRequest;
import com.masil.backend.dto.request.MasilPasswordChangeRequest;
import com.masil.backend.dto.request.MasilProfileUpdateRequest;
import com.masil.backend.dto.response.MasilProfileUpdateResponse;
import com.masil.backend.dto.response.MasilUserResponse;
import com.masil.backend.service.MasilUserDetailService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MasilUserController {

	private final MasilUserDetailService userDetailService;

	// 유저정보조회
    @GetMapping("/api/auth/user/user-info")
    public ResponseEntity<?> getUserDetail(@RequestBody MasilMemberDetailRequest memberDetail) {
        MasilUserResponse userResponse = userDetailService.getUserDetail(memberDetail.getNickName());
        return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "유저 정보 조회 성공", userResponse);
    }

    // 비밀번호변경
    @PostMapping("/api/auth/user/passwordchange")
    public ResponseEntity<?> changePassword(@RequestBody MasilPasswordChangeRequest passWordChange) {
        userDetailService.passWordChange(passWordChange);
        return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "비밀번호 변경 성공");
    }

    // 프로필 변경
    @PostMapping("/api/auth/user/profile")
    public ResponseEntity<?> updateProfile(@RequestPart("profileImage") MultipartFile profileImage,@RequestPart("user_email") String userEmail,@RequestPart("statusMessage") String statusMessage) {
		MasilProfileUpdateRequest profileUpdateRequest = new MasilProfileUpdateRequest(userEmail, profileImage, statusMessage);
		MasilProfileUpdateResponse response = userDetailService.updateUserProfile(profileUpdateRequest);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "유저 정보 수정 성공", response);
	}
}