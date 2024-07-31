package com.masil.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasilProfileUpdateResponse {
	private String profileImageUrl;
    private String nickName;
    private String statusMessage;

}
