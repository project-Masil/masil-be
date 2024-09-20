package com.masil.backend.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
@Builder
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	private String reviewContent;

	private String postWriter;

	private String reviewTags;

	private float reviewScore;

	private LocalDateTime reviewCreated;

	private LocalDateTime reviewUpdated;

	@ManyToOne
	@JoinColumn(name = "cafe_id")
	private CafeInfo cafeInfo;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ReviewImage> reviewImages;

	public void setReviewImagesFromUrls(List<String> imageUrls) {
        if (reviewImages != null) {
            reviewImages.clear();
        } else {
            reviewImages = new HashSet<>();
        }

        for (String imageUrl : imageUrls) {
            ReviewImage reviewImage = ReviewImage.builder()
                .review(this)
                .reviewimgName(imageUrl)
                .deleteYn(false)
                .build();
            reviewImages.add(reviewImage);
        }
    }
}