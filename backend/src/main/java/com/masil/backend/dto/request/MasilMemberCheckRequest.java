package com.masil.backend.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MasilMemberCheckRequest {
	@Email
	@NotEmpty(message = "이메일을 입력해 주세요")
	private String email;

	@NotEmpty(message = "인증 번호를 입력해 주세요")
	private String authNum;

	@NotEmpty(message = "닉네임을 입력해 주세요")
	private String nickName;

	@NotEmpty(message = "비밀번호를 입력해 주세요")
	private String password;

	private String statusMessage;

    private MultipartFile profile;
}
