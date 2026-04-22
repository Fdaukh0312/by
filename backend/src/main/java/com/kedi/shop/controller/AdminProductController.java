package com.kedi.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kedi.shop.entity.Product;
import com.kedi.shop.service.ProductService;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(productService.getAdminProductPage(pageNum, pageSize, categoryId, keyword, status));
    }

    @GetMapping("/detail/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @PostMapping
    public Result<Void> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<Void> updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success("更新成功", null);
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功", null);
    }
}
