package com.kedi.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kedi.shop.entity.Category;
import com.kedi.shop.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> getCategoryList() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        return this.list(wrapper);
    }

    public List<Category> getAdminCategoryList() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort");
        return this.list(wrapper);
    }

    public void addCategory(Category category) {
        this.save(category);
    }

    public void updateCategory(Category category) {
        this.updateById(category);
    }

    public void deleteCategory(Long id) {
        this.removeById(id);
    }
}
