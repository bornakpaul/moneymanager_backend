package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.ExpenseDto;
import com.apptechlab.moneymanager.dto.FilterDto;
import com.apptechlab.moneymanager.dto.IncomeDto;
import com.apptechlab.moneymanager.service.ExpenseService;
import com.apptechlab.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filter){
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.of(1900, 1, 1);
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.of(3000, 1, 1);
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if("income".equals(filter.getType())){
            List<IncomeDto> incomes = incomeService.filterIncomes(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(incomes);
        }else if("expense".equals(filter.getType())){
            List<ExpenseDto> expenses = expenseService.filterExpenses(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expenses);
        } else{
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
        }
    }
}
