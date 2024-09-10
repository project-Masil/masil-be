package com.masil.backend.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
	private Long id;
	private String content;
	//private List<ReviewImageDto> reviewImageUrls;
	private String cafeName;
	private float rating;
	private List<String> tags;
}