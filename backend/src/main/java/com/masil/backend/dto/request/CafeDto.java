package com.masil.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeDto {
	private Long id;
	private String cafeName;
	private String cafeLoca;
	private String cafeSignature;
	private Float cafeOpen;
}