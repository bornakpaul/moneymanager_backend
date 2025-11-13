package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.CategoryDto;
import com.apptechlab.moneymanager.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category Controller",description = "API's to create, fetch and update categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
}
