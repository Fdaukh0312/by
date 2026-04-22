package com.kedi.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kedi.shop.entity.Order;
import com.kedi.shop.entity.Product;
import com.kedi.shop.entity.User;
import com.kedi.shop.mapper.OrderMapper;
import com.kedi.shop.mapper.ProductMapper;
import com.kedi.shop.mapper.UserMapper;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 商品总数
        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.eq("deleted", 0);
        stats.put("productCount", productMapper.selectCount(productWrapper));

        // 上架商品数
        QueryWrapper<Product> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("deleted", 0).eq("status", 1);
        stats.put("activeProductCount", productMapper.selectCount(activeWrapper));

        // 用户总数
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("deleted", 0).eq("role", 1);
        stats.put("userCount", userMapper.selectCount(userWrapper));

        // 订单总数
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("deleted", 0);
        stats.put("orderCount", orderMapper.selectCount(orderWrapper));

        // 待处理订单数（待支付+待发货）
        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("deleted", 0).in("status", 1, 2);
        stats.put("pendingOrderCount", orderMapper.selectCount(pendingWrapper));

        // 今日销售额
        stats.put("todaySales", orderMapper.selectTodaySales());

        // 总销售额
        QueryWrapper<Order> salesWrapper = new QueryWrapper<>();
        salesWrapper.eq("deleted", 0).eq("status", 4);
        BigDecimal totalSales = orderMapper.selectTotalSales();
        stats.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);

        return Result.success(stats);
    }
}
