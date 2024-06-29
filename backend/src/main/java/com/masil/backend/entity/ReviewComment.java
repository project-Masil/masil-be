package com.masil.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;

	private String commentWriter;

	private String commentContent;

	private LocalDateTime commentCreated;

	private LocalDateTime commentUpdated;
}