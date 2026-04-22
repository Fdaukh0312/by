package com.kedi.shop.entity;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class OrderCreateDTO {
    private String remark;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
}
