package com.kedi.shop.controller;

import com.kedi.shop.entity.Category;
import com.kedi.shop.service.CategoryService;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        return Result.success(categoryService.getCategoryList());
    }

    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<Void> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
