package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.ExpenseDto;
import com.apptechlab.moneymanager.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
@Tag(name = "Expense Controllers",description = "API's to add, fetch and delete expenses of individual users")
public class ExpenseController {

    private final ExpenseService expenseService;


    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto dto){
        ExpenseDto saved = expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(){
        List<ExpenseDto> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
