package com.masil.backend.controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.MasilLoginRequest;
import com.masil.backend.dto.response.MasilLoginTokenData;
import com.masil.backend.entity.MasilMember;
import com.masil.backend.entity.MasilMemberLoginTryCount;
import com.masil.backend.repository.MasilMemberLoginTryCountRepository;
import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.service.MasilAuthService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.ErrorCode;
import com.masil.backend.util.Formatter.ResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;
import com.masil.backend.util.Jwt.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class MasilAuthController {

	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MasilAuthService userAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MasilMemberRepository memberRepository;

    @Autowired
    private MasilMemberLoginTryCountRepository loginTryCountRepository;

    // 로그인
    @PostMapping("/api/auth/user/sign-in")
    public ResponseEntity<ResponseBodyFormatter> login(@RequestBody MasilLoginRequest loginRequest, HttpServletResponse response) throws Exception {
    	Optional<MasilMemberLoginTryCount> optionalLoginTryCount = loginTryCountRepository.findByUserEmail(loginRequest.getEmail());

    	// 로그인 시도 실패 카운트 조회
        MasilMemberLoginTryCount loginTryCount = optionalLoginTryCount.orElse(MasilMemberLoginTryCount.builder()
                .userEmail(loginRequest.getEmail())
                .loginFailCount(0)
                .loginIsLock('N')
                .lockCount(5) // 기본 잠금 횟수 설정
                .build());

        // 계정이 잠겨 있는지 확인하고, 잠금 시간이 지났는지 확인
        if (loginTryCount.getLoginIsLock() == 'Y') {
            long lockDuration = 5 * 60 * 1000; // 5분
            if (System.currentTimeMillis() - loginTryCount.getLastLoginDate().getTime() < lockDuration) {
                return DataResponseBodyFormatter.init(ErrorCode.USER_NOT_FOUND, "계정이 잠겼습니다. 잠금 해제까지 기다려주세요.");
            } else {
                // 잠금 해제
                loginTryCount.setLoginIsLock('N');
                loginTryCount.setLoginFailCount(0);
            }
        }

        try {
            // 사용자 이메일과 비밀번호로 인증 시도
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // 로그인 성공 시, 로그인 시도 실패 카운트 초기화
            loginTryCount.setLoginFailCount(0);
            loginTryCount.setLoginIsLock('N');
            loginTryCount.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
            loginTryCountRepository.save(loginTryCount);
        } catch (AuthenticationException e) {
        	// 인증 실패 시, 실패 횟수 증가 및 잠금 처리
            loginTryCount.setLoginFailCount(loginTryCount.getLoginFailCount() + 1);
            loginTryCount.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
            if (loginTryCount.getLoginFailCount() >= loginTryCount.getLockCount()) {
                loginTryCount.setLoginIsLock('Y');
            }
            loginTryCountRepository.save(loginTryCount);
            return DataResponseBodyFormatter.init(ErrorCode.USER_NOT_FOUND, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 인증 성공 시 사용자 세부 정보 로드
        final UserDetails userDetails = userAuthService.loadUserByUsername(loginRequest.getEmail());

        // 사용자 정보에서 userId 추출
        final MasilMember member = memberRepository.findByUserEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginRequest.getEmail()));

        final String userId = member.getUserId();

        // JWT 액세스 토큰 및 리프레시 토큰 생성
        final String accessToken = jwtUtil.generateAccessToken(userDetails, userId);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, userId);

        // JWT의 유효 시간을 쿠키의 유효 시간으로 설정
        int accessTokenCookieMaxAge = (int) jwtUtil.getAccessTokenValidity();
        int refreshTokenCookieMaxAge = (int) jwtUtil.getRefreshTokenValidity();

        // Access Token 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTokenCookieMaxAge);

        // Refresh Token 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTokenCookieMaxAge);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // JSON 응답 생성
        MasilLoginTokenData tokenData = new MasilLoginTokenData(accessToken, refreshToken);
        return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, tokenData);
    }

    // 로그아웃
    @PostMapping("/api/auth/user/sign-out")
    public ResponseEntity<ResponseBodyFormatter> logout(HttpServletRequest request, HttpServletResponse response) {
        // JWT 토큰을 요청에서 추출합니다.
        String token = jwtUtil.extractTokenFromHeader(request);

        // 추출한 토큰이 유효하고 블랙리스트에 추가되지 않았다면 로그아웃 처리합니다.
        if (token != null && !jwtUtil.isTokenBlacklisted(token)) {
            jwtUtil.addToBlacklist(token);

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

            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "로그아웃되었습니다.");
        } else {
            // 유효하지 않은 토큰이거나 이미 블랙리스트에 추가된 경우 처리
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "로그아웃 중 오류가 발생하였습니다.");
        }
    }
}
