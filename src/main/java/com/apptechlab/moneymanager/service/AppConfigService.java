package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.AppConfigDto;
import com.apptechlab.moneymanager.entity.AppConfig;
import com.apptechlab.moneymanager.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private AppConfigRepository appConfigRepository;

    public AppConfigDto getVersions(){
        List<AppConfig> configs = appConfigRepository.findAll();

        AppConfigDto response = new AppConfigDto();

        for(AppConfig config: configs){
            AppConfigDto.PlatformDetailDto details = AppConfigDto.PlatformDetailDto
                    .builder()
                    .minVersion(config.getMinVersion())
                    .storeUrl(config.getStoreUrl())
                    .build();

            if("android".equalsIgnoreCase(config.getPlatform())){
                response.setAndroid(details);
            }else if("ios".equalsIgnoreCase(config.getPlatform())){
                response.setIos(details);
            }
        }
        return response;
    }
}
