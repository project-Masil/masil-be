package com.masil.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cafeId;

	private String cafeName;

	private String cafeLoca;

	private String cafeSignature;

	private Float cafeOpen;

	@OneToMany(mappedBy = "cafeInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Review> reviews;
}