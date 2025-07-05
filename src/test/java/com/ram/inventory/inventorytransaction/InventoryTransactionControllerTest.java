package com.ram.inventory.inventorytransaction;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.inventory.product.Product;
import com.ram.inventory.util.PaginationResponse;

@WebMvcTest(InventoryTransactionController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryTransactionService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "create transaction should create a transaction")
    void createOrder() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryDTOId = UUID.randomUUID();
        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        request.setType("T");

        Product product = new Product();
        product.setProductId(productId);

        InventoryTransactionDTO inventoryDTO = new InventoryTransactionDTO();
        inventoryDTO.setTransactionId(inventoryDTOId);
        inventoryDTO.setQuantity(1);
        inventoryDTO.setProduct(product);
        inventoryDTO.setType(InventoryTransactionType.TOPUP);

        Mockito.when(inventoryService.createInventoryTransaction(any(InventoryTransactionRequest.class))).thenReturn(inventoryDTO);

        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Transaction Created"))
                .andExpect(jsonPath("$.data.transactionId").value(inventoryDTOId.toString()));
    }

    @Test
    @DisplayName(value = "get transaction should return transaction based on id")
    void getOrder() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryDTOId = UUID.randomUUID();
        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        request.setType("T");

        Product product = new Product();
        product.setProductId(productId);

        InventoryTransactionDTO inventoryDTO = new InventoryTransactionDTO();
        inventoryDTO.setTransactionId(inventoryDTOId);
        inventoryDTO.setQuantity(1);
        inventoryDTO.setProduct(product);
        inventoryDTO.setType(InventoryTransactionType.TOPUP);

        Mockito.when(inventoryService.getTransaction(productId)).thenReturn(inventoryDTO);

        mockMvc.perform(get("/transaction/get-transaction").param("id", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Transaction Found"))
                .andExpect(jsonPath("$.data.transactionId").value(inventoryDTOId.toString()));
    }

    @Test
    @DisplayName(value = "get transactions should return all transactions")
    void getOrders() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryDTOId = UUID.randomUUID();
        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        request.setType("T");

        Product product = new Product();
        product.setProductId(productId);

        InventoryTransactionDTO inventoryDTO = new InventoryTransactionDTO();
        inventoryDTO.setTransactionId(inventoryDTOId);
        inventoryDTO.setQuantity(1);
        inventoryDTO.setProduct(product);
        inventoryDTO.setType(InventoryTransactionType.TOPUP);
        PaginationResponse<InventoryTransactionDTO> response = new PaginationResponse<>(
                Collections.singletonList(inventoryDTO),
                new PaginationResponse.PaginationInfo(
                        0,
                        0,
                        1,
                        1L));

        Mockito.when(inventoryService.getTransactions(0, 10, "transactionId")).thenReturn(response);

        mockMvc.perform(get("/transaction/get-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Transactions Found"))
                .andExpect(jsonPath("$.data.data[0].transactionId").value(inventoryDTOId.toString()));
    }

    @Test
    @DisplayName(value = "update transaction should update fields in transaction")
    void updateOrders() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID inventoryDTOId = UUID.randomUUID();
        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        request.setType("T");

        Product product = new Product();
        product.setProductId(productId);

        InventoryTransactionDTO inventoryDTO = new InventoryTransactionDTO();
        inventoryDTO.setTransactionId(inventoryDTOId);
        inventoryDTO.setQuantity(1);
        inventoryDTO.setProduct(product);
        inventoryDTO.setType(InventoryTransactionType.TOPUP);

        Mockito.when(inventoryService.updateTransaction(any(InventoryTransactionDTO.class))).thenReturn(inventoryDTO);

        mockMvc.perform(post("/transaction/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Transaction Updated"))
                .andExpect(jsonPath("$.data.transactionId").value(inventoryDTOId.toString()));
    }

    @Test
    @DisplayName(value = "delete transaction should delete transaction")
    void deleteOrders() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("id", "0cdebff1-cc6c-47c2-bacb-340cc9da8fe3");

        mockMvc.perform(post("/transaction/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Transaction Deleted"));
    }
}