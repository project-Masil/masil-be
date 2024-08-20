package com.masil.backend.service.impl;

import com.masil.backend.dto.request.CafeDto;
import com.masil.backend.entity.CafeInfo;
import com.masil.backend.repository.CafeInfoRepository;
import com.masil.backend.service.CafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CafeServiceImpl implements CafeService {

	@Autowired
	private CafeInfoRepository cafeInfoRepository;

	@Override
	public List<CafeInfo> getAllCafes() {
		return cafeInfoRepository.findAll();
	}

	@Override
	public Optional<CafeInfo> getCafeById(Long id) {
		return cafeInfoRepository.findById(id);
	}

	@Override
	public List<CafeInfo> getLikedCafesByUserId(Long userId) {
		// 사용자별로 좋아요한 카페 조회 구현 (예시)
		return cafeInfoRepository.findLikedCafesByUserId(userId);
	}

	@Override
	public List<CafeInfo> searchCafes(String query) {
		// 카페 검색 구현 (예시)
		return cafeInfoRepository.searchCafesByName(query);
	}

	@Override
	public CafeInfo createCafe(CafeDto cafeDto) {
		CafeInfo cafeInfo = CafeInfo.builder()
			.cafeName(cafeDto.getCafeName())
			.cafeLoca(cafeDto.getCafeLoca())
			.cafeSignature(cafeDto.getCafeSignature())
			.cafeOpen(cafeDto.getCafeOpen())
			.build();

		return cafeInfoRepository.save(cafeInfo);
	}

	@Override
	public List<CafeInfo> getRecommendedCafes() {
		// 추천 카페 조회 로직 구현 (예시)
		return cafeInfoRepository.findRecommendedCafes();
	}
}