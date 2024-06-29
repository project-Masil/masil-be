package com.masil.backend.service;

import com.masil.backend.dto.request.CafeDto;
import com.masil.backend.entity.CafeInfo;

import java.util.List;
import java.util.Optional;

public interface CafeService {
	List<CafeInfo> getAllCafes();
	Optional<CafeInfo> getCafeById(Long id);
	List<CafeInfo> getLikedCafesByUserId(Long userId);
	List<CafeInfo> searchCafes(String query);
	CafeInfo createCafe(CafeDto cafeDTO);
	List<CafeInfo> getRecommendedCafes();
}