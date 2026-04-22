package com.kedi.shop.controller;

import com.kedi.shop.entity.Cart;
import com.kedi.shop.entity.CartDTO;
import com.kedi.shop.service.CartService;
import com.kedi.shop.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public Result<List<Cart>> getCartList(@RequestAttribute Long userId) {
        return Result.success(cartService.getCartList(userId));
    }

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestAttribute Long userId, @RequestBody CartDTO cartDTO) {
        try {
            cartService.addToCart(userId, cartDTO.getProductId(), cartDTO.getQuantity());
            return Result.success("加入购物车成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Result<Void> updateCartQuantity(@RequestAttribute Long userId,
                                           @PathVariable Long id,
                                           @RequestParam Integer quantity) {
        try {
            cartService.updateCartQuantity(userId, id, quantity);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeFromCart(@RequestAttribute Long userId, @PathVariable Long id) {
        try {
            cartService.removeFromCart(userId, id);
            return Result.success("移除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestAttribute Long userId) {
        cartService.clearCart(userId);
        return Result.success("清空成功", null);
    }
}
