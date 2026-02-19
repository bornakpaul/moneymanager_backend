package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.CategoryDto;
import com.apptechlab.moneymanager.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(){
        List<CategoryDto> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByTypeAndUser(@PathVariable String type){
        List<CategoryDto> categories = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto){
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId,categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }
}
