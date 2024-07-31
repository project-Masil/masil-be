package com.masil.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.masil.backend.entity.MasilProfileStatus;

@Repository
public interface MasilProfileStatusRepository extends JpaRepository<MasilProfileStatus, String> {

}
