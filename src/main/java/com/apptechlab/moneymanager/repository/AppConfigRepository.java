package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepository extends JpaRepository<AppConfig,Long> {
    Optional<AppConfig> findByPlatform(String platform);
}
