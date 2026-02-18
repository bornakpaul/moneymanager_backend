package com.apptechlab.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfigDto {

    private PlatformDetailDto android;
    private PlatformDetailDto ios;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformDetailDto {
        private String minVersion;
        private String storeUrl;
    }
}
