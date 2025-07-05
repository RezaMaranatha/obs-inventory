package com.ram.inventory.order;

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
import com.ram.inventory.product.Product;
import com.ram.inventory.util.PaginationResponse;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "create order should create a order")
    void createOrder() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID orderDTOId = UUID.randomUUID();
        OrderRequest request = new OrderRequest();
        request.setProductId(productId);
        request.setQuantity(1);

        Product product = new Product();
        product.setProductId(productId);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderDTOId);
        orderDTO.setQuantity(1);
        orderDTO.setPrice(new BigDecimal(5));
        orderDTO.setProduct(product);

        Mockito.when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order Created"))
                .andExpect(jsonPath("$.data.orderId").value(orderDTOId.toString()));
    }

    @Test
    @DisplayName(value = "get order should return order based on id")
    void getOrder() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID orderDTOId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderDTOId);
        orderDTO.setQuantity(1);
        orderDTO.setPrice(new BigDecimal(5));
        orderDTO.setProduct(product);

        Mockito.when(orderService.getOrder(productId)).thenReturn(orderDTO);

        mockMvc.perform(get("/order/get-order").param("id", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order Found"))
                .andExpect(jsonPath("$.data.orderId").value(orderDTOId.toString()));
    }

    @Test
    @DisplayName(value = "get orders should return all orders")
    void getOrders() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID orderDTOId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderDTOId);
        orderDTO.setQuantity(1);
        orderDTO.setPrice(new BigDecimal(5));
        orderDTO.setProduct(product);
        PaginationResponse<OrderDTO> response = new PaginationResponse<>(
                Collections.singletonList(orderDTO),
                new PaginationResponse.PaginationInfo(
                        0,
                        0,
                        1,
                        1L));

        Mockito.when(orderService.getOrders(0, 10, "orderId")).thenReturn(response);

        mockMvc.perform(get("/order/get-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Orders Found"))
                .andExpect(jsonPath("$.data.data[0].orderId").value(orderDTOId.toString()));
    }

    @Test
    @DisplayName(value = "update order should update fields in order")
    void updateOrders() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID orderDTOId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderDTOId);
        orderDTO.setQuantity(1);
        orderDTO.setPrice(new BigDecimal(5));
        orderDTO.setProduct(product);

        Mockito.when(orderService.updateOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/order/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order Updated"))
                .andExpect(jsonPath("$.data.orderId").value(orderDTOId.toString()));
    }

    @Test
    @DisplayName(value = "delete order should delete order")
    void deleteOrders() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("id", "0cdebff1-cc6c-47c2-bacb-340cc9da8fe3");

        mockMvc.perform(post("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order Deleted"));
    }
}