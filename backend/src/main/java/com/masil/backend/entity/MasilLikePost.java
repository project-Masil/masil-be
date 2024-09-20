package com.masil.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "greate_post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasilLikePost {
	@Id
    @Column(name = "user_email")
    private String userEmail;	// 유저 이메일

	@Column(name = "like_post")
	private int likePost;	// 찜한 게시글번호

	@Column(name = "great_post")
	private int greatPost;	// 좋아요한 게시글 번호


}
