package com.masil.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.ReviewComment;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
	List<ReviewComment> findByReview_ReviewId(Long reviewId);
}