package com.masil.backend.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
	private List<MultipartFile> reviewImages;
	private String cafeName;
	private float rating;
	private List<String> tags;
}