package com.kedi.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kedi.shop.entity.Order;
import com.kedi.shop.entity.OrderItem;
import com.kedi.shop.service.OrderService;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(orderService.getAdminOrderPage(pageNum, pageSize, status, keyword));
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        return Result.success(orderService.getOrderItems(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateOrderStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
