package com.kedi.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kedi.shop.entity.Product;
import com.kedi.shop.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    public Page<Product> getProductPage(Integer pageNum, Integer pageSize, Long categoryId, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        wrapper.orderByDesc("create_time");
        return this.page(page, wrapper);
    }

    public Page<Product> getAdminProductPage(Integer pageNum, Integer pageSize, Long categoryId, String keyword, Integer status) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return this.page(page, wrapper);
    }

    public Product getProductDetail(Long id) {
        return this.getById(id);
    }

    public void addProduct(Product product) {
        product.setStatus(1);
        this.save(product);
    }

    public void updateProduct(Product product) {
        this.updateById(product);
    }

    public void updateProductStatus(Long id, Integer status) {
        Product product = this.getById(id);
        if (product != null) {
            product.setStatus(status);
            this.updateById(product);
        }
    }

    public void deleteProduct(Long id) {
        this.removeById(id);
    }

    public List<Product> getRecommendProducts(int limit) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT " + limit);
        return this.list(wrapper);
    }
}
