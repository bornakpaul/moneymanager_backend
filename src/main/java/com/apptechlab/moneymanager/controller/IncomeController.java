package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.IncomeDto;
import com.apptechlab.moneymanager.service.IncomeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
@Tag(name = "Income Controllers",description = "API's to add, fetch and delete incomes of individual users")
public class IncomeController {

    private final IncomeService incomeService;


    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto dto){
        IncomeDto saved = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getExpenses(){
        List<IncomeDto> incomes = incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(incomes);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
