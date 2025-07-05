package com.ram.inventory.inventorytransaction;

import com.ram.inventory.exception.ResourceNotFoundException;
import com.ram.inventory.product.Product;
import com.ram.inventory.product.ProductRepository;
import com.ram.inventory.util.PaginationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryTransactionServiceTest {

    private InventoryTransactionRepository inventoryTransactionRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private InventoryTransactionService inventoryTransactionService;

    @BeforeEach
    void setUp() {
        inventoryTransactionRepository = mock(InventoryTransactionRepository.class);
        productRepository = mock(ProductRepository.class);
        modelMapper = new ModelMapper();
        inventoryTransactionService = new InventoryTransactionService(inventoryTransactionRepository, productRepository,
                modelMapper);
    }

    @Test
    void createInventoryTransaction_withWithdraw_shouldSucceed() throws Exception {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setProductId(productId);
        product.setName("Item");
        product.setPrice(BigDecimal.valueOf(100));
        product.setCurrentQuantity(10);

        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setType("W");
        request.setQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(product)).thenReturn(product);

        InventoryTransactionDTO dto = inventoryTransactionService.createInventoryTransaction(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getQuantity()).isEqualTo(5);
        assertThat(product.getCurrentQuantity()).isEqualTo(5); // quantity reduced
        verify(productRepository).save(product);
        verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void createInventoryTransaction_withTopUp_shouldSucceed() throws Exception {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setProductId(productId);
        product.setName("Item");
        product.setPrice(BigDecimal.valueOf(100));
        product.setCurrentQuantity(5);

        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(productId);
        request.setType("T");
        request.setQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.save(product)).thenReturn(product);

        InventoryTransactionDTO dto = inventoryTransactionService.createInventoryTransaction(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getQuantity()).isEqualTo(5);
        assertThat(product.getCurrentQuantity()).isEqualTo(10);
        verify(productRepository).save(product);
        verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void createInventoryTransaction_shouldThrow_whenTypeMissingOrInvalid() {
        InventoryTransactionRequest request = new InventoryTransactionRequest();
        request.setProductId(UUID.randomUUID());
        request.setType("X");
        request.setQuantity(5);

        Product product = new Product();
        product.setCurrentQuantity(10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> inventoryTransactionService.createInventoryTransaction(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction Type missing");
    }

    @Test
    void getTransaction_shouldReturnDTO_whenFound() throws Exception {
        UUID id = UUID.randomUUID();
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionId(id)
                .quantity(3)
                .build();

        when(inventoryTransactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        InventoryTransactionDTO dto = inventoryTransactionService.getTransaction(id);

        assertThat(dto).isNotNull();
        assertThat(dto.getTransactionId()).isEqualTo(id);
        assertThat(dto.getQuantity()).isEqualTo(3);
    }

    @Test
    void getTransaction_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(inventoryTransactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryTransactionService.getTransaction(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found");
    }

    @Test
    void getTransactions_shouldReturnPaginatedResponse() {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionId(UUID.randomUUID())
                .quantity(1)
                .build();

        Page<InventoryTransaction> page = new PageImpl<>(List.of(transaction), PageRequest.of(0, 10), 1);

        when(inventoryTransactionRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginationResponse<InventoryTransactionDTO> response = inventoryTransactionService.getTransactions(0, 10,
                "quantity");

        assertThat(response.getData()).hasSize(1);
        assertThat(response.getPagination().getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateTransaction_shouldUpdateAndReturnDTO() throws Exception {
        UUID id = UUID.randomUUID();

        InventoryTransaction existing = InventoryTransaction.builder()
                .transactionId(id)
                .quantity(2)
                .build();

        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setTransactionId(id);
        dto.setQuantity(10);

        when(inventoryTransactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(inventoryTransactionRepository.save(any(InventoryTransaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        InventoryTransactionDTO updated = inventoryTransactionService.updateTransaction(dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getQuantity()).isEqualTo(10);
    }

    @Test
    void updateTransaction_shouldThrow_whenIdMissing() {
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setTransactionId(null);

        assertThatThrownBy(() -> inventoryTransactionService.updateTransaction(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction ID missing");
    }

    @Test
    void updateTransaction_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setTransactionId(id);

        when(inventoryTransactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryTransactionService.updateTransaction(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found");
    }

    @Test
    void deleteTransaction_shouldCallDeleteById() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(inventoryTransactionRepository).deleteById(id);

        inventoryTransactionService.deleteTransaction(id);

        verify(inventoryTransactionRepository).deleteById(id);
    }

    @Test
    void deleteTransaction_shouldThrow_whenDeleteFails() {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("fail")).when(inventoryTransactionRepository).deleteById(id);

        assertThatThrownBy(() -> inventoryTransactionService.deleteTransaction(id))
                .isInstanceOf(Exception.class);
    }
}