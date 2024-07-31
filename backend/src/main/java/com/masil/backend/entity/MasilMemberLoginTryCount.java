package com.masil.backend.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_try_count")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasilMemberLoginTryCount {
	@Id
    @Column(name = "user_email")
    private String userEmail;	// 이메일

	@Column(name = "login_file_count")
    private Integer loginFailCount;	// 로그인 실패 횟수

	@Column(name = "login_is_lock")
    private char loginIsLock;	// 로그인 시도 제한

	@Column(name = "last_login_date")
    private Timestamp lastLoginDate;	// 최근 접속 시도 시간

	@Column(name = "lock_count")
    private Integer lockCount;	// 로그인 시도 제한 횟수(기본값 5회)
}
