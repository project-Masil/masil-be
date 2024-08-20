package com.masil.backend.dto.request;

import java.util.List;

import com.masil.backend.entity.ReviewImage;
import com.masil.backend.util.Enum.Moods;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateReviewRequestDto {

	private String content;
	private ReviewImage reviewImage;
	private String cafeName;
	private int rating;
	private List<Moods> tags;
}
