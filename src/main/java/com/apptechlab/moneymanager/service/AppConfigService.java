package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.AppConfigDto;
import com.apptechlab.moneymanager.entity.AppConfigEntity;
import com.apptechlab.moneymanager.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private final AppConfigRepository appConfigRepository;

    public AppConfigDto getVersions(){
        AppConfigEntity androidConfig = appConfigRepository.findByPlatform("android");
        AppConfigEntity iosConfig = appConfigRepository.findByPlatform("ios");

        AppConfigDto response = new AppConfigDto();
        AppConfigDto.PlatformDetailDto androidDetails = AppConfigDto.PlatformDetailDto
                .builder()
                .minVersion(androidConfig.getMinVersion())
                .storeUrl(androidConfig.getStoreUrl())
                .build();
        AppConfigDto.PlatformDetailDto iosDetails = AppConfigDto.PlatformDetailDto
                .builder()
                .minVersion(iosConfig.getMinVersion())
                .storeUrl(iosConfig.getStoreUrl())
                .build();
        response.setAndroid(androidDetails);
        response.setIos(iosDetails);
        return response;
    }
}
