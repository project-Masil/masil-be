package com.masil.backend.repository;

import com.masil.backend.entity.CafeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CafeInfoRepository extends JpaRepository<CafeInfo, Long> {
	Optional<CafeInfo> findByCafeName(String cafeName);
}