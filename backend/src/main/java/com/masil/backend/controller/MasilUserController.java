package com.masil.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.MasilLikeRequest;
import com.masil.backend.dto.request.MasilPasswordChangeRequest;
import com.masil.backend.dto.request.MasilProfileUpdateRequest;
import com.masil.backend.dto.response.MasilProfileUpdateResponse;
import com.masil.backend.dto.response.MasilUserResponse;
import com.masil.backend.service.MasilUserDetailService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.ErrorCode;
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
		MasilProfileUpdateResponse response = userDetailService.updateUserProfile(profileUpdateRequest);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "유저 정보 수정 성공", response);
	}

    // 찜하기 OR 좋아요 추가
    @PostMapping("/api/auth/post/like")
    public ResponseEntity<?> likePost(@RequestBody MasilLikeRequest likeRequest) {
        String userEmail = likeRequest.getUserEmail();
        int postId = likeRequest.getPostId();
        String postType = likeRequest.getPostType();  // "좋아요" 또는 "찜하기"

        if ("like".equals(postType)) {
        	// 찜하기 여부 확인 후 처리
            if (userDetailService.isPostLikedByUser(userEmail, postId)) {
            	userDetailService.cancelLikeOrGreatPost(userEmail, true); // 찜 취소
                return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "게시글 찜을 취소했습니다.");
            } else {
            	userDetailService.likePost(userEmail, postId); // 찜 추가
                return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "게시글을 찜했습니다.");
            }
        } else if ("great".equals(postType)) {
            // 좋아요 여부 확인 후 처리
        	if (userDetailService.isPostBookmarkedByUser(userEmail, postId)) {
        		userDetailService.cancelLikeOrGreatPost(userEmail, false); // 좋아요 취소
                return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "게시글 좋아요를 취소했습니다.");
            } else {
            	userDetailService.greatPost(userEmail, postId); // 좋아요 추가
                return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "게시글을 좋아요 했습니다.");
            }
        } else {
            return DataResponseBodyFormatter.init(ErrorCode.NOT_EXIST, "잘못된 요청입니다.");
        }
    }

    // 찜하기 OR 좋아요 조회
    @GetMapping("/api/auth/post/likelist")
    public ResponseEntity<?> getLikedOrBookmarkedPosts(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) String type) {

        String userEmail = userDetails.getUsername();

        // 찜하기만 조회
        if ("like".equals(type)) {
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "찜한 게시글 조회 성공", userDetailService.getLikedPosts(userEmail));
        }

        // 좋아요만 조회
        else if ("great".equals(type)) {
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "좋아요한 게시글 조회 성공", userDetailService.getGreatPosts(userEmail));
        }

        // 찜하기와 좋아요 모두 조회
        else {
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "찜한 및 좋아요한 게시글 조회 성공", userDetailService.getAllLikedAndGreatPosts(userEmail));
        }
    }
}