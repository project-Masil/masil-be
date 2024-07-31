package com.masil.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MasilLoginTokenData {
	private String accessToken;
    private String refreshToken;
}
