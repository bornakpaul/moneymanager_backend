package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long> {

    // select * from tbl_profiles where email = ?
   Optional<ProfileEntity> findByEmail(String email);
   Optional<ProfileEntity> findByActivationToken(String activationToken);
   Optional<ProfileEntity> findById(Long profileId);
   Optional<ProfileEntity> findByResetPasswordToken(String token);
}
