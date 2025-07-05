package com.ram.inventory.product;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.ram.inventory.util.PaginationResponse;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "create product should create a product")
    void createProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setName("Test Product");
        productDTO.setDescription("Some description");
        productDTO.setPrice(BigDecimal.valueOf(99.99));
        productDTO.setCurrentQuantity(100);

        Mockito.when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Product Created"))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(99.99));
    }

    @Test
    @DisplayName(value = "get product should return product based on id")
    void getProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setName("Test Product");
        productDTO.setDescription("Some description");
        productDTO.setPrice(BigDecimal.valueOf(99.99));
        productDTO.setCurrentQuantity(100);

        Mockito.when(productService.getProduct(productId)).thenReturn(productDTO);

        mockMvc.perform(get("/product/get-product").param("id", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Product Found"))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(99.99));
    }

    @Test
    @DisplayName(value = "get products should return all products")
    void getProducts() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setName("Test Product");
        productDTO.setDescription("Some description");
        productDTO.setPrice(BigDecimal.valueOf(99.99));
        productDTO.setCurrentQuantity(100);
        PaginationResponse<ProductDTO> response = new PaginationResponse<>(
                Collections.singletonList(productDTO),
                new PaginationResponse.PaginationInfo(
                        0,
                        0,
                        1,
                        1L));

        Mockito.when(productService.getProducts(0, 10, "name")).thenReturn(response);

        mockMvc.perform(get("/product/get-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Products Found"))
                .andExpect(jsonPath("$.data.data.length()").value(1))
                .andExpect(jsonPath("$.data.data[0].price").value(99.99));
    }

    @Test
    @DisplayName(value = "update product should update fields in product")
    void updateProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setName("Test Product");
        productDTO.setDescription("Some description");
        productDTO.setPrice(BigDecimal.valueOf(99.99));
        productDTO.setCurrentQuantity(100);

        Mockito.when(productService.updateProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/product/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Product Updated"))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(99.99));
    }

    @Test
    @DisplayName(value = "delete product should delete product")
    void deleteProduct() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("id","0cdebff1-cc6c-47c2-bacb-340cc9da8fe3");

        mockMvc.perform(post("/product/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Product Deleted"));
    }
}