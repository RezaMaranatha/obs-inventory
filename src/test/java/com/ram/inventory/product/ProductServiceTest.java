package com.ram.inventory.product;

import com.ram.inventory.exception.ResourceNotFoundException;
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

class ProductServiceTest {

    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        modelMapper = new ModelMapper();
        productService = new ProductService(productRepository, modelMapper);
    }

    @Test
    void createProduct_shouldSaveAndReturnDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Phone");
        dto.setPrice(BigDecimal.valueOf(500));
        dto.setCurrentQuantity(10);

        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(500));
        product.setCurrentQuantity(10);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(dto);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(product.getProductId());
        assertThat(result.getName()).isEqualTo("Phone");
    }

    @Test
    void getProduct_shouldReturnProductDTO_whenFound() {
        UUID id = UUID.randomUUID();

        Product product = new Product();
        product.setProductId(id);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setCurrentQuantity(5);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductDTO dto = productService.getProduct(id);

        assertThat(dto).isNotNull();
        assertThat(dto.getProductId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Laptop");
    }

    @Test
    void getProduct_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    void getProducts_shouldReturnPaginationResponse() {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Monitor");
        product.setPrice(BigDecimal.valueOf(200));
        product.setCurrentQuantity(3);

        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginationResponse<ProductDTO> response = productService.getProducts(0, 10, "name");

        assertThat(response.getData()).hasSize(1);
        assertThat(response.getPagination().getTotalElements()).isEqualTo(1);
        assertThat(response.getData().get(0).getName()).isEqualTo("Monitor");
    }

    @Test
    void updateProduct_shouldUpdateAndReturnDTO() {
        UUID id = UUID.randomUUID();

        Product existingProduct = new Product();
        existingProduct.setProductId(UUID.randomUUID());
        existingProduct.setName("Old name");
        existingProduct.setPrice(BigDecimal.valueOf(100));
        existingProduct.setCurrentQuantity(2);

        ProductDTO updateDto = new ProductDTO();
        updateDto.setProductId(id);
        updateDto.setName("New Name");
        updateDto.setPrice(BigDecimal.valueOf(150));
        updateDto.setCurrentQuantity(5);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO updated = productService.updateProduct(updateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getPrice()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(updated.getCurrentQuantity()).isEqualTo(5);
    }

    @Test
    void updateProduct_shouldThrow_whenProductIdMissing() {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(null);

        assertThatThrownBy(() -> productService.updateProduct(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product ID missing");
    }

    @Test
    void updateProduct_shouldThrow_whenProductNotFound() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = new ProductDTO();
        dto.setProductId(id);

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found");
    }

    @Test
    void deleteProduct_shouldCallRepositoryDelete() {
        UUID id = UUID.randomUUID();
        productService.deleteProduct(id);
        verify(productRepository).deleteById(id);
    }
}