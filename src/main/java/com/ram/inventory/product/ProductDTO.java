package com.ram.inventory.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductDTO {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer currentQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}