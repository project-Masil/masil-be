package com.masil.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masil.backend.dto.request.ReviewCommentDto;
import com.masil.backend.dto.request.ReviewDto;
import com.masil.backend.entity.Review;
import com.masil.backend.entity.ReviewComment;
import com.masil.backend.service.ReviewService;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import com.masil.backend.util.Formatter.ErrorCode;
import com.masil.backend.util.Formatter.ResponseBodyFormatter;
import com.masil.backend.util.Formatter.SuccessCode;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@GetMapping
	public ResponseEntity<ResponseBodyFormatter> getAllReviews(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewService.getAllReviews(pageable);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, reviews);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseBodyFormatter> getReviewById(@PathVariable Long id) {
		Review review = reviewService.getReviewById(id).orElse(null);
		if (review == null) {
			return ResponseBodyFormatter.init(ErrorCode.NOT_EXIST);
		}
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, review);
	}

	@GetMapping("/comment/{reviewId}")
	public ResponseEntity<ResponseBodyFormatter> getCommentsByReviewId(@PathVariable Long reviewId) {
		List<ReviewComment> comments = reviewService.getCommentsByReviewId(reviewId);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, comments);
	}

	@PostMapping
	public ResponseEntity<ResponseBodyFormatter> createReview(@RequestBody ReviewDto reviewDto) {
		Review review = reviewService.createReview(reviewDto);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, review);
	}

	@PatchMapping
	public ResponseEntity<ResponseBodyFormatter> updateReview(@RequestBody ReviewDto reviewDto) {
		Review review = reviewService.updateReview(reviewDto.getId(), reviewDto);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, review);
	}

	@DeleteMapping
	public ResponseEntity<ResponseBodyFormatter> deleteReview(@RequestParam Long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseBodyFormatter.init(SuccessCode.SUCCESS);
	}

	@PostMapping("/comment")
	public ResponseEntity<ResponseBodyFormatter> createReviewComment(@RequestBody ReviewCommentDto reviewCommentDto) {
		ReviewComment reviewComment = reviewService.createReviewComment(reviewCommentDto);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, reviewComment);
	}

	@PatchMapping("/comment")
	public ResponseEntity<ResponseBodyFormatter> updateReviewComment(@RequestBody ReviewCommentDto reviewCommentDTO) {
		ReviewComment reviewComment = reviewService.updateReviewComment(reviewCommentDTO.getId(), reviewCommentDTO);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, reviewComment);
	}

	@DeleteMapping("/comment")
	public ResponseEntity<ResponseBodyFormatter> deleteReviewComment(@RequestParam Long commentId) {
		reviewService.deleteReviewComment(commentId);
		return ResponseBodyFormatter.init(SuccessCode.SUCCESS);
	}
}