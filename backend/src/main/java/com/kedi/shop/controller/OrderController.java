package com.kedi.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kedi.shop.entity.Order;
import com.kedi.shop.entity.OrderCreateDTO;
import com.kedi.shop.entity.OrderItem;
import com.kedi.shop.service.OrderService;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Order> createOrder(@RequestAttribute Long userId, @RequestBody OrderCreateDTO orderDTO) {
        try {
            Order order = orderService.createOrder(userId, orderDTO);
            return Result.success("下单成功", order);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.getOrderPage(userId, pageNum, pageSize));
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        return Result.success(orderService.getOrderItems(id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@RequestAttribute Long userId, @PathVariable Long id) {
        try {
            orderService.cancelOrder(userId, id);
            return Result.success("订单已取消", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
