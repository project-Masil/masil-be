package com.masil.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ReviewImage> reviewImages;
}