package com.ram.inventory.order;

import java.util.UUID;

import lombok.Data;

@Data
public class OrderRequest {
    private UUID productId;
    private int quantity;
}