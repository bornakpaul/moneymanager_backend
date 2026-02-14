package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.ExpenseDto;
import com.apptechlab.moneymanager.dto.IncomeDto;
import com.apptechlab.moneymanager.dto.MonthlyAnalyticsDto;
import com.apptechlab.moneymanager.dto.RecentTransactionDto;
import com.apptechlab.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue =  new LinkedHashMap<>();
        List<IncomeDto> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDto> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();
        List<RecentTransactionDto> recentTransaction = concat(latestIncomes.stream().map(income -> RecentTransactionDto.builder()
                .id(income.getId())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .name(income.getName())
                .categoryName(income.getCategoryName())
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .type("income")
                        .profileId(profile.getId())
                        .build()),
                latestExpenses.stream().map(expense -> RecentTransactionDto.builder()
                .id(expense.getId())
                .icon(expense.getIcon())
                .amount(expense.getAmount())
                .name(expense.getName())
                .categoryName(expense.getCategoryName())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .type("expense")
                        .profileId(profile.getId())
                        .build())
        ).sorted((a,b) ->{
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
        }).toList();
        returnValue.put("totalBalance", incomeService.getTotalIncomeForCurrentUser().subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense",expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expenses",latestExpenses);
        returnValue.put("recent5Incomes",latestIncomes);
        returnValue.put("recentTransactions",recentTransaction);
        returnValue.put("expenseForTheMonth", expenseService.getCurrentMonthExpensesForCurrentUser());
        returnValue.put("incomeForTheMonth",  incomeService.getCurrentMonthIncomeForCurrentUser());
        returnValue.put("dashboardAnalytics", getDashboardAnalytics());
        return returnValue;
    }

    public List<MonthlyAnalyticsDto> getDashboardAnalytics() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        // Logic: Jan-Jun (1-6) or Jul-Dec (7-12)
        int startMonth = (now.getMonthValue() <= 6) ? 1 : 7;
        int endMonth = (now.getMonthValue() <= 6) ? 6 : 12;

        LocalDate startDate = LocalDate.of(currentYear, startMonth, 1);
        LocalDate endDate = LocalDate.of(currentYear, endMonth, startDate.withMonth(endMonth).lengthOfMonth());

        // Call individual services
        Map<Integer, BigDecimal> expenses = expenseService.getExpenseAnalyticsData(startDate, endDate);
        Map<Integer, BigDecimal> incomes = incomeService.getIncomeAnalyticsData(startDate, endDate);

        List<MonthlyAnalyticsDto> finalData = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            finalData.add(MonthlyAnalyticsDto.builder()
                    .month(Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .income(incomes.getOrDefault(m, BigDecimal.ZERO))
                    .expense(expenses.getOrDefault(m, BigDecimal.ZERO))
                    .build());
        }
        return finalData;
    }
}
