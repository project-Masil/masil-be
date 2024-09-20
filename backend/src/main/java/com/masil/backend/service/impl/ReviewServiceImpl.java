package com.masil.backend.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.masil.backend.dto.request.ReviewCommentDto;
import com.masil.backend.dto.request.ReviewDto;
import com.masil.backend.entity.CafeInfo;
import com.masil.backend.entity.Review;
import com.masil.backend.entity.ReviewComment;
import com.masil.backend.entity.ReviewImage;
import com.masil.backend.repository.CafeInfoRepository;
import com.masil.backend.repository.ReviewCommentRepository;
import com.masil.backend.repository.ReviewRepository;
import com.masil.backend.service.ReviewService;
import com.masil.backend.service.S3Service;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private CafeInfoRepository cafeInfoRepository;

	@Autowired
	private ReviewCommentRepository reviewCommentRepository;

	@Autowired
    private S3Service s3Service;

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

		// 파일이 존재하면 S3에 업로드 후 이미지 설정
		if (reviewDto.getReviewImages() != null && !reviewDto.getReviewImages().isEmpty()) {
	        for (MultipartFile file : reviewDto.getReviewImages()) {
	            try {
	                String imageUrl = s3Service.uploadFile(file);
	                long fileSize = file.getSize();
	                ReviewImage reviewImage = ReviewImage.builder()
	                    .review(review)
	                    .reviewimgName(imageUrl)
	                    .reviewimgSize(fileSize)
	                    .deleteYn(false)
	                    .build();
	                review.getReviewImages().add(reviewImage);
	            } catch (IOException e) {
	                throw new RuntimeException("파일 업로드 실패", e);
	            }
	        }
	    }
		return reviewRepository.save(review);
	}

	@Override
	public Review updateReview(Long id, ReviewDto reviewDto) {
		Review review = reviewRepository.findById(id).orElseThrow();
		review.setReviewContent(reviewDto.getContent());
		review.setReviewScore(reviewDto.getRating());
		review.setReviewTags(String.join(",", reviewDto.getTags()));

		review.getReviewImages().clear();

		if (reviewDto.getReviewImages() != null && !reviewDto.getReviewImages().isEmpty()) {
	        for (MultipartFile file : reviewDto.getReviewImages()) {
	            try {
	                String imageUrl = s3Service.uploadFile(file);
	                long fileSize = file.getSize();
	                ReviewImage reviewImage = ReviewImage.builder()
	                    .review(review)
	                    .reviewimgName(imageUrl)
	                    .reviewimgSize(fileSize)
	                    .deleteYn(false)
	                    .build();
	                review.getReviewImages().add(reviewImage);
	            } catch (IOException e) {
	                throw new RuntimeException("파일 업로드 실패", e);
	            }
	        }
	    }

		return reviewRepository.save(review);
	}

	@Override
	public void deleteReview(Long id) {
		Review review = reviewRepository.findById(id).orElseThrow();

		for (ReviewImage reviewImage : review.getReviewImages()) {
	        try {
	            s3Service.deleteFile(reviewImage.getReviewimgName());
	        } catch (IOException e) {
	            throw new RuntimeException("이미지 삭제 실패: " + reviewImage.getReviewimgName(), e);
	        }
	    }

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