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
@Table(name = "user_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasilProfileStatus {

	@Id
    @Column(name = "user_email")
    private String userEmail;	// 이메일

    @Column(name = "profile_imgname")
    private String profileImgName;	// 프로필 이미지명

    @Column(name = "profile_reimgname")
    private String profileReImgName;	// 저장된이미지명

    @Column(name = "profile_size")
    private Long profileSize;	// 파일크기

    @Column(name = "profile_msg")
    private String profileMsg;	// 상태메세지

    @Column(name = "profile_path")
    private String profileImgPath;	// 이미지 저장 경로
}
