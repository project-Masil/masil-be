package com.masil.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masil.backend.dto.request.ReviewCommentDto;
import com.masil.backend.dto.request.ReviewDto;
import com.masil.backend.entity.CafeInfo;
import com.masil.backend.entity.Review;
import com.masil.backend.entity.ReviewComment;
import com.masil.backend.repository.CafeInfoRepository;
import com.masil.backend.repository.ReviewCommentRepository;
import com.masil.backend.repository.ReviewRepository;
import com.masil.backend.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private CafeInfoRepository cafeInfoRepository;

	@Autowired
	private ReviewCommentRepository reviewCommentRepository;

	@Override
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	@Override
	public Optional<Review> getReviewById(Long id) {
		return reviewRepository.findById(id);
	}

	@Override
	public Page<Review> getAllReviews(Pageable pageable) {
		return reviewRepository.findAll(pageable);
	}

	@Override
	public List<ReviewComment> getCommentsByReviewId(Long reviewId) {
		return reviewCommentRepository.findByReview_ReviewId(reviewId);
	}

	@Override
	public Review createReview(ReviewDto reviewDto) {
		CafeInfo cafeInfo = cafeInfoRepository.findByCafeName(reviewDto.getCafeName())
			.orElseGet(() -> {
				CafeInfo newCafe = new CafeInfo();
				newCafe.setCafeName(reviewDto.getCafeName());
				return cafeInfoRepository.save(newCafe);
			});

		Review review = Review.builder()
			.reviewContent(reviewDto.getContent())
			.cafeInfo(cafeInfo)
			.reviewScore(reviewDto.getRating())
			.reviewTags(String.join(",", reviewDto.getTags()))
			.build();
		return reviewRepository.save(review);
	}

	@Override
	public Review updateReview(Long id, ReviewDto reviewDto) {
		Review review = reviewRepository.findById(id).orElseThrow();
		review.setReviewContent(reviewDto.getContent());
		review.setReviewScore(reviewDto.getRating());
		review.setReviewTags(String.join(",", reviewDto.getTags()));
		return reviewRepository.save(review);
	}

	@Override
	public void deleteReview(Long id) {
		reviewRepository.deleteById(id);
	}

	@Override
	public ReviewComment createReviewComment(ReviewCommentDto reviewCommenwDto) {
		Review review = reviewRepository.findById(reviewCommenwDto.getReviewId()).orElseThrow();

		ReviewComment reviewComment = ReviewComment.builder()
			.review(review)
			.commentContent(reviewCommenwDto.getContent())
			.build();

		return reviewCommentRepository.save(reviewComment);
	}

	@Override
	public ReviewComment updateReviewComment(Long id, ReviewCommentDto reviewCommenwDto) {
		ReviewComment reviewComment = reviewCommentRepository.findById(id).orElseThrow();
		reviewComment.setCommentContent(reviewCommenwDto.getContent());

		return reviewCommentRepository.save(reviewComment);
	}

	@Override
	public void deleteReviewComment(Long id) {
		reviewCommentRepository.deleteById(id);
	}
}