package com.masil.backend.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Builder
public class CafeInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cafeId;

	private String cafeName;

	private String cafeLoca;

	private String cafeSigniture;

	private Float cafeOpen;

	@OneToMany(mappedBy = "cafeInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Review> reviews;
}