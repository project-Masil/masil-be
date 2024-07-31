package com.masil.backend.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MasilProfileUpdateRequest {
	@Email
	private String user_email;

	private MultipartFile profileImage;

	private String statusMessage;

}
