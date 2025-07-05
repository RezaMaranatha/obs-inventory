package com.ram.inventory.inventorytransaction;

public enum InventoryTransactionType {
    TOPUP("T"),
    WITHDRAW("W");

    private final String code;

    InventoryTransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
