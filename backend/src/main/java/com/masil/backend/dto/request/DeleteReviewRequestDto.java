package com.masil.backend.dto.request;

import java.util.List;

import com.masil.backend.entity.ReviewImage;
import com.masil.backend.util.Enum.Moods;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeleteReviewRequestDto {

	private Long reviewId;

}
