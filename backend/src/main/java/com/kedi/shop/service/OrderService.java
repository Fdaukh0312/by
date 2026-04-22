package com.kedi.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kedi.shop.entity.*;
import com.kedi.shop.mapper.CartMapper;
import com.kedi.shop.mapper.OrderItemMapper;
import com.kedi.shop.mapper.OrderMapper;
import com.kedi.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public Order createOrder(Long userId, OrderCreateDTO orderDTO) {
        QueryWrapper<Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        List<Cart> carts = cartMapper.selectList(cartWrapper);

        if (carts == null || carts.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架: " + cart.getProductId());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(1);
        order.setReceiverName(orderDTO.getReceiverName());
        order.setReceiverPhone(orderDTO.getReceiverPhone());
        order.setReceiverAddress(orderDTO.getReceiverAddress());
        order.setRemark(orderDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.save(order);

        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);

            product.setStock(product.getStock() - cart.getQuantity());
            productMapper.updateById(product);
        }

        QueryWrapper<Cart> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId);
        cartMapper.delete(deleteWrapper);

        return order;
    }

    public Page<Order> getOrderPage(Long userId, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return this.page(page, wrapper);
    }

    public Page<Order> getAdminOrderPage(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("order_no", keyword)
                    .or().like("receiver_name", keyword)
                    .or().like("receiver_phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        return this.page(page, wrapper);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        return orderItemMapper.selectList(wrapper);
    }

    public void cancelOrder(Long userId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许取消");
        }
        order.setStatus(5);
        this.updateById(order);
    }

    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return date + uuid;
    }
}
