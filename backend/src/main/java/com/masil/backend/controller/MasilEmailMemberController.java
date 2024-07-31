package com.masil.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.MasilMemberCheckRequest;
import com.masil.backend.service.MasilMailSendService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.ErrorCode;
import com.masil.backend.util.Formatter.ResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MasilEmailMemberController {

    private final MasilMailSendService mailService;

    // 이메일 인증번호 발송
    @PostMapping("/api/auth/mailSend")
    public ResponseEntity<ResponseBodyFormatter> mailSend(@RequestBody @Valid MasilMemberCheckRequest emailDto) {
        if (mailService.isEmailExists(emailDto.getEmail())) {
            return DataResponseBodyFormatter.init(ErrorCode.NOT_EXIST, "이메일이 이미 존재합니다.");
        }
        String authNumber = mailService.joinEmail(emailDto.getEmail());
        return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, authNumber);
    }

    // 이메일 인증번호 확인
    @PostMapping("/api/auth/mailauthCheck")
    public ResponseEntity<ResponseBodyFormatter> authCheck(@RequestBody @Valid MasilMemberCheckRequest emailCheckDto) {
        Boolean checked = mailService.CheckAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if (checked) {
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "ok");
        } else {
            return DataResponseBodyFormatter.init(ErrorCode.NOT_PERMISSION, "인증 실패!");
        }
    }

    // 이메일 인증번호 재발송
    @PostMapping("/api/auth/resendMail")
    public ResponseEntity<ResponseBodyFormatter> resendMail(@RequestBody @Valid MasilMemberCheckRequest emailDto) {
    	if (mailService.isEmailExists(emailDto.getEmail())) {
            return DataResponseBodyFormatter.init(ErrorCode.NOT_EXIST, "이메일이 이미 존재합니다.");
        }
        String authNumber = mailService.resendEmail(emailDto.getEmail());
        return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, authNumber);
    }
}