package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.AnalyticsOverviewDto;
import com.apptechlab.moneymanager.dto.AnalyticsOverviewRequestDto;
import com.apptechlab.moneymanager.service.AnalyticsService;
import com.apptechlab.moneymanager.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Controller",description = "API to fetch all the data for dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashboardData(){
        Map<String,Object> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @PostMapping("/overview")
    public ResponseEntity<AnalyticsOverviewDto> getDashboardOverviewData(@RequestBody AnalyticsOverviewRequestDto anayticsOverviewRequest){
        AnalyticsOverviewDto analyticsOverviewData = analyticsService.getDetailedAnalytics(anayticsOverviewRequest);
        return ResponseEntity.ok(analyticsOverviewData);
    }
}
