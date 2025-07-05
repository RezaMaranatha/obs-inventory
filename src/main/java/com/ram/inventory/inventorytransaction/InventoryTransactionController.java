package com.ram.inventory.inventorytransaction;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ram.inventory.util.ApiResponse;
import com.ram.inventory.util.PaginationResponse;

@RestController
@RequestMapping("/transaction")
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    public InventoryTransactionController(InventoryTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryTransactionDTO>> createInventoryTransaction(@RequestBody InventoryTransactionRequest request) throws Exception {
        return ResponseEntity
				.ok(new ApiResponse<>(200, "Transaction Created", transactionService.createInventoryTransaction(request)));
    }

    @GetMapping(value = "/get-transactions")
    public ResponseEntity<ApiResponse<PaginationResponse<InventoryTransactionDTO>>> getTransactions(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "transactionId") String sortBy) {
        return ResponseEntity
				.ok(new ApiResponse<>(200, "Transactions Found", transactionService.getTransactions(pageNumber, pageSize, sortBy)));
    }

    @GetMapping(value = "/get-transaction")
    public ResponseEntity<ApiResponse<InventoryTransactionDTO>> getTransaction(@RequestParam UUID id) throws Exception {
        return ResponseEntity
				.ok(new ApiResponse<>(200, "Transaction Found", transactionService.getTransaction(id)));
    }

    @PostMapping(value = "/update")
    public ResponseEntity<ApiResponse<InventoryTransactionDTO>> updateTransaction(@RequestBody InventoryTransactionDTO product) throws Exception {
        return ResponseEntity
				.ok(new ApiResponse<>(200, "Transaction Updated", transactionService.updateTransaction(product)));
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(@RequestBody Map<String, String> request) throws Exception {
        transactionService.deleteTransaction(UUID.fromString(request.get("id")));
        return ResponseEntity
				.ok(new ApiResponse<>(200, "Transaction Deleted", "Success"));
    }
}