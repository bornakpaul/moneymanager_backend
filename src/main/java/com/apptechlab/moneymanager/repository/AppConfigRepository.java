package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.AppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepository extends JpaRepository<AppConfigEntity,Long> {
    Optional<AppConfigEntity> findByPlatform(String platform);
}
