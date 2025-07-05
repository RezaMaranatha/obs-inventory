package com.ram.inventory.inventorytransaction;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ram.inventory.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue
    private UUID transactionId;

    @ManyToOne
    private Product product;

    private InventoryTransactionType type;

    private int quantity;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}