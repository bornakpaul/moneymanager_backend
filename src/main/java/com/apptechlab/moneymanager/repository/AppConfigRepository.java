package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.AppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfigEntity,Long> {
    AppConfigEntity findByPlatform(String platform);
}
