package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.AnalyticsOverviewDto;
import com.apptechlab.moneymanager.dto.AnalyticsOverviewRequestDto;
import com.apptechlab.moneymanager.repository.ExpenseRepository;
import com.apptechlab.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public AnalyticsOverviewDto getDetailedAnalytics(AnalyticsOverviewRequestDto analyticsOverviewRequestDto) {
        Long profileId = profileService.getCurrentProfile().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(analyticsOverviewRequestDto.getRange(), endDate);

        // 1. Fetch Summary Totals for the Cards
        BigDecimal totalIncome = incomeRepository.findTotalIncomeByProfileIdAndDateBetween(profileId, startDate, endDate);
        BigDecimal totalExpense = expenseRepository.findTotalExpenseByProfileIdAndDateBetween(profileId, startDate, endDate);

        // 2. Fetch Bar Chart Data
        List<AnalyticsOverviewDto.ChartDataPoint> chartData = fetchChartData(startDate, endDate, analyticsOverviewRequestDto.getRange(), analyticsOverviewRequestDto.getCategoryType());

        // 3. Fetch Donut Chart Data (Breakdown)
        List<AnalyticsOverviewDto.BreakdownDto> breakdown = fetchBreakdownData(profileId, startDate, endDate, analyticsOverviewRequestDto.getCategoryType());

        return new AnalyticsOverviewDto(
                chartData, totalIncome,totalExpense,breakdown);
    }

    private List<AnalyticsOverviewDto.BreakdownDto> fetchBreakdownData(Long profileId, LocalDate start, LocalDate end, String type) {
        List<Object[]> results = new ArrayList<>();

        // 1. Fetch raw totals per category based on the filter type
        if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase("expense")) {
            results.addAll(expenseRepository.aggregateByCategory(profileId, start, end));
        }
        if (type.equalsIgnoreCase("income")) {
            results.addAll(incomeRepository.aggregateByCategory(profileId, start, end));
        }

        // 2. Calculate the grand total for percentage calculation
        BigDecimal grandTotal = results.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Map to DTO and calculate percentages
        return results.stream().map(row -> {
            String categoryName = (String) row[0];
            BigDecimal categoryAmount = (BigDecimal) row[1];

            double percentage = 0.0;
            if (grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                percentage = categoryAmount.divide(grandTotal, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .doubleValue();
            }

            return new AnalyticsOverviewDto.BreakdownDto(categoryName, categoryAmount, percentage);
        }).collect(Collectors.toList());
    }


    private List<AnalyticsOverviewDto.ChartDataPoint> fetchChartData( LocalDate s, LocalDate e, String range, String type) {
        // 1. Fetch maps from both services
        Map<String, BigDecimal> incomeMap = new HashMap<>();
        Map<String, BigDecimal> expenseMap = new HashMap<>();

        if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase("income")) {
            incomeMap = incomeService.getAggregatedData(s, e, range);
        }
        if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase("expense")) {
            expenseMap = expenseService.getAggregatedData(s, e, range);
        }

        List<AnalyticsOverviewDto.ChartDataPoint> chartData = new ArrayList<>();

        // 2. Iterate through the timeline to fill gaps
        if ("12months".equalsIgnoreCase(range)) {
            // Monthly loop: Start date to End date, month by month
            for (LocalDate date = s; !date.isAfter(e); date = date.plusMonths(1)) {
                String key = date.getYear() + "-" + date.getMonthValue();
                chartData.add(new AnalyticsOverviewDto.ChartDataPoint(
                        date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), // "Jan"
                        incomeMap.getOrDefault(key, BigDecimal.ZERO),
                        expenseMap.getOrDefault(key, BigDecimal.ZERO)
                ));
            }
        } else {
            // Daily loop: For 7 or 28 days
            for (LocalDate date = s; !date.isAfter(e); date = date.plusDays(1)) {
                String key = date.toString(); // "2026-02-19"
                chartData.add(new AnalyticsOverviewDto.ChartDataPoint(
                        formatDailyLabel(date, range),
                        incomeMap.getOrDefault(key, BigDecimal.ZERO),
                        expenseMap.getOrDefault(key, BigDecimal.ZERO)
                ));
            }
        }

        return chartData;
    }

    private LocalDate calculateStartDate(String range, LocalDate endDate) {
        return switch (range.toLowerCase()) {
            case "7days" -> endDate.minusDays(6); // Current day + 6 days back
            case "28days" -> endDate.minusDays(27);
            case "12months" -> endDate.minusMonths(11).withDayOfMonth(1); // Start of the month 11 months ago
            default -> endDate.minusMonths(11).withDayOfMonth(1); // Default to 12 months
        };
    }

    private String formatDailyLabel(LocalDate date, String range) {
        if ("7days".equalsIgnoreCase(range)) {
            return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // "Mon"
        }
        return date.getDayOfMonth() + " " + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // "19 Feb"
    }
}
