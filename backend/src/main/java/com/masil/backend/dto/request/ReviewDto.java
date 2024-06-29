package com.masil.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
	private Long id;
	private String content;
	private List<ReviewImageDto> reviewImageUrls;
	private String cafeName;
	private float rating;
	private List<String> tags;
}