package com.masil.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasilUserResponse {
	private String userId;            // String 타입으로 수정
    private String profileImage;      // 파일 이름으로 저장
    private String profileImagePath;
    private String nickName;
    private String statusMessage;

}
