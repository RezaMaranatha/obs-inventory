package com.ram.inventory.inventorytransaction;

import java.util.UUID;

import lombok.Data;

@Data
public class InventoryTransactionRequest {
    private UUID productId;
    private String type;
    private int quantity;
}