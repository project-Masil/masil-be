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
@Table(name = "member")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasilMember {

	@Id
    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_number")
    private Integer userNumber;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pwd")
    private String userPwd;

    @Column(name = "user_role")
    private Integer userRole;

    @Column(name = "signup_date")
    private Timestamp signupDate;
}
