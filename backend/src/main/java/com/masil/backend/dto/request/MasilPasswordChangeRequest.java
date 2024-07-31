package com.masil.backend.dto.request;

import lombok.Data;

@Data
public class MasilPasswordChangeRequest {
	private String oldPassword;
    private String newPassword;
}
