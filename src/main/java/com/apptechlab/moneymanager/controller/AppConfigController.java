package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.AppConfigDto;
import com.apptechlab.moneymanager.service.AppConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app-config")
@CrossOrigin(origins = "*")
@Tag(name = "App config Controller",description = "API's fetch app version for force update")
public class AppConfigController {
    private AppConfigService appConfigService;

    @GetMapping("/version")
    public ResponseEntity<AppConfigDto> getAppVersionConfig(){
        AppConfigDto config = appConfigService.getVersions();
        if(config.getAndroid() == null && config.getIos() == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(config);
    }
}
