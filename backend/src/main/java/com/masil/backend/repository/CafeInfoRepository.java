package com.masil.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.CafeInfo;

@Repository
public interface CafeInfoRepository extends JpaRepository<CafeInfo, Long> {
	Optional<CafeInfo> findByCafeName(String cafeName);
	List<CafeInfo> findByCafeNameContaining(String query);

	// 카페 리뷰 평점을 기준으로 정렬
	@Query(value = "SELECT c.* FROM cafe_info c JOIN reviews r ON c.cafe_id = r.cafe_id GROUP BY c.cafe_id ORDER BY AVG(r.review_score) DESC, MIN(r.review_created) ASC", nativeQuery = true)
	List<CafeInfo> findRecommendedCafes();
}