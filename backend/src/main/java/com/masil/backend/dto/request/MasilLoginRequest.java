package com.masil.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasilLoginRequest {
	private String email;
    private String password;
}
