package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.entity.RefreshTokenEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByProfile(ProfileEntity profile);

    void deleteByProfileId(Long profileId);

}
