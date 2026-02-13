package com.apptechlab.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class MonthlyAnalyticsDto {
    private String month; // e.g., "Jan", "Feb"
    private BigDecimal income;
    private BigDecimal expense;
}
