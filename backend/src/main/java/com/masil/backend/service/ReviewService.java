package com.masil.backend.service;

import com.masil.backend.dto.request.ReviewCommentDto;
import com.masil.backend.dto.request.ReviewDto;
import com.masil.backend.entity.Review;
import com.masil.backend.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
	List getAllReviews();
	Optional getReviewById(Long id);
	Page getAllReviews(Pageable pageable);
	List getCommentsByReviewId(Long reviewId);
	Review createReview(ReviewDto reviewDTO);
	Review updateReview(Long id, ReviewDto reviewDTO);
	void deleteReview(Long id);
	ReviewComment createReviewComment(ReviewCommentDto reviewCommentDTO);
	ReviewComment updateReviewComment(Long id, ReviewCommentDto reviewCommentDTO);
	void deleteReviewComment(Long id);
}