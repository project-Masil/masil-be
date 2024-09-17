package com.masil.backend.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.MasilMemberCheckRequest;
import com.masil.backend.service.MasilSignMemberService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.ErrorCode;
import com.masil.backend.util.Formatter.ResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MasilSignMemberController {

	private final MasilSignMemberService memberSignService;

	// 회원가입
    @PostMapping("/api/auth/user/sign-up")
    public ResponseEntity<ResponseBodyFormatter> registerAndUploadProfile(@Valid @RequestPart("member") MasilMemberCheckRequest memberCheckDto) {
    	if (memberSignService.isIdExists(memberCheckDto.getNickName())) {
            return DataResponseBodyFormatter.init(ErrorCode.NOT_EXIST, "닉네임이 이미 존재합니다.");
        }
	    try {
	        // 회원 가입 처리
	        memberSignService.registerMember(memberCheckDto);
	    } catch (IOException e) {
	        return DataResponseBodyFormatter.init(ErrorCode.WRONG_JSON_FORMAT, "프로필 이미지 저장 중 오류가 발생했습니다.");
	    }

	    return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "가입 성공");
    }

    // 회원탈퇴
    @PostMapping("/api/auth/user/resign")
    public ResponseEntity<?> resignMember(@AuthenticationPrincipal UserDetails userDetails,@RequestHeader("Authorization") String token, HttpServletResponse response) {
    	String actualToken = token.substring(7);

    	// 회원탈퇴 서비스 호출
    	memberSignService.resignMember(userDetails.getUsername(), actualToken);

    	// Access Token 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 쿠키 만료 설정

        // Refresh Token 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 쿠키 만료 설정

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

    	// 성공 응답 반환
    	return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "회원탈퇴 성공");
    }

}
