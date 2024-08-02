package com.masil.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.MasilMember;

@Repository
public interface MasilMemberRepository extends JpaRepository<MasilMember, String> {
	// 이메일 중복 확인
    boolean existsByUserEmail(String userEmail);

    // 닉네임 중복 확인
    boolean existsByUserId(String userId);

    // 유저번호
    @Query("SELECT COALESCE(MAX(m.userNumber), 0) FROM MasilMember m")
    int findMaxUserNumber();

    // 로그인 유저 조회
    Optional<MasilMember> findByUserEmail(String userEmail);
}
