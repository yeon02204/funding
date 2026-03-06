package com.funding.funding.domain.category.controller;

import com.funding.funding.domain.category.entity.Category;
import com.funding.funding.domain.category.service.CategoryService;
import com.funding.funding.global.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<Category>> getCategories() {
        return ApiResponse.ok(categoryService.getAll());
    }
}