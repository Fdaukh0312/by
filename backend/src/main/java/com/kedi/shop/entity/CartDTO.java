package com.kedi.shop.entity;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CartDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
