package com.masil.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.MasilPasswordChangeRequest;
import com.masil.backend.dto.request.MasilProfileUpdateRequest;
import com.masil.backend.dto.response.MasilProfileUpdateResponse;
import com.masil.backend.dto.response.MasilUserResponse;
import com.masil.backend.service.MasilUserDetailService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MasilUserController {

	private final MasilUserDetailService userDetailService;

	// 유저정보조회
    @GetMapping("/api/auth/user/user-info")
    public ResponseEntity<?> getUserDetail(@AuthenticationPrincipal UserDetails userDetails) {
        MasilUserResponse userResponse = userDetailService.getUserDetail(userDetails.getUsername());
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
    public ResponseEntity<?> updateProfile(@ModelAttribute @Valid MasilProfileUpdateRequest profileUpdateRequest) {
    	profileUpdateRequest.setUser_email(profileUpdateRequest.getUser_email());
		MasilProfileUpdateResponse response = userDetailService.updateUserProfile(profileUpdateRequest);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "유저 정보 수정 성공", response);
	}
}