package com.ram.inventory.inventorytransaction;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ram.inventory.product.Product;

import lombok.Data;

@Data
public class InventoryTransactionDTO {
    private UUID transactionId;
    private Product product;
    private InventoryTransactionType type;
    private int quantity;
    private LocalDateTime createdAt;
}