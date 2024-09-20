package com.masil.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasilLikeRequest {
	private String userEmail;
    private int postId;
    private String postType; // "좋아요" 또는 "찜하기"
}
