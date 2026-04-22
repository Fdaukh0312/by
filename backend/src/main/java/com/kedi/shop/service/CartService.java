package com.kedi.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kedi.shop.entity.Cart;
import com.kedi.shop.entity.Product;
import com.kedi.shop.mapper.CartMapper;
import com.kedi.shop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {

    @Autowired
    private ProductMapper productMapper;

    public List<Cart> getCartList(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Cart> carts = this.list(wrapper);
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
        }
        return carts;
    }

    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        Cart existCart = this.getOne(wrapper);

        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + quantity);
            this.updateById(existCart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            this.save(cart);
        }
    }

    @Transactional
    public void updateCartQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车记录不存在");
        }
        if (quantity <= 0) {
            this.removeById(cartId);
        } else {
            cart.setQuantity(quantity);
            this.updateById(cart);
        }
    }

    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车记录不存在");
        }
        this.removeById(cartId);
    }

    public void clearCart(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        this.remove(wrapper);
    }
}
