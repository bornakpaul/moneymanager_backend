package com.apptechlab.moneymanager.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsOverviewDto {
    private List<ChartDataPoint> chartData;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<BreakdownDto> breakdown;


    @Data
    @AllArgsConstructor
    public static class ChartDataPoint {
        private String label;
        private BigDecimal income;
        private BigDecimal expense;
    }

    @Data
    @AllArgsConstructor
    public static class BreakdownDto {
        private String categoryName;
        private BigDecimal amount;
        private Double percentage;
    }
}
