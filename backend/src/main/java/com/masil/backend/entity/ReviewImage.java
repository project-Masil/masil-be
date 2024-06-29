package com.masil.backend.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;

	private String reviewingName;

	private Long reviewingSize;

	private Boolean deleteYn;
}