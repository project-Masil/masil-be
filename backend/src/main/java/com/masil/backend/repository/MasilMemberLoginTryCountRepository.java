package com.masil.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.MasilMemberLoginTryCount;

@Repository
public interface MasilMemberLoginTryCountRepository extends JpaRepository<MasilMemberLoginTryCount, String> {
    Optional<MasilMemberLoginTryCount> findByUserEmail(String userEmail);
}
