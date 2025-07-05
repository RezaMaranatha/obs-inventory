package com.ram.inventory.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ram.inventory.product.Product;

import lombok.Data;

@Data
public class OrderDTO {
    private UUID orderId;

    private Product product;
    private Integer quantity;
    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}