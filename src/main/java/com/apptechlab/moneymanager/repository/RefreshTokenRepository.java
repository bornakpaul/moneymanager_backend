package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByProfile(ProfileEntity profile);

    void deleteByProfileId(Long profileId);

}
