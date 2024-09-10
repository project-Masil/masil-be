package com.masil.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.MasilLikePost;

@Repository
public interface MasilLikePostRepository extends JpaRepository<MasilLikePost, String> {
	boolean existsByUserEmailAndLikePost(String userEmail, int postId);
    boolean existsByUserEmailAndGreatPost(String userEmail, int postId);
    // 찜한 게시글 조회
    List<MasilLikePost> findByUserEmailAndLikePostNotNull(String userEmail);

    // 좋아요한 게시글 조회
    List<MasilLikePost> findByUserEmailAndGreatPostNotNull(String userEmail);
}
