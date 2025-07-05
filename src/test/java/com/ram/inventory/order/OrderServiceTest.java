package com.ram.inventory.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ram.inventory.exception.ResourceNotFoundException;
import com.ram.inventory.product.Product;
import com.ram.inventory.product.ProductRepository;
import com.ram.inventory.util.PaginationResponse;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        modelMapper = new ModelMapper();
        orderService = new OrderService(orderRepository, modelMapper, productRepository);
    }

    @Test
    void createOrder_shouldSucceed_whenProductExistsAndEnoughStock() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(200));
        product.setCurrentQuantity(10);

        OrderRequest request = new OrderRequest();
        request.setProductId(productId);
        request.setQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(200));
        verify(productRepository).save(product);
    }

    @Test
    void createOrder_shouldThrowException_whenStockIsInsufficient() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(200));
        product.setCurrentQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setProductId(productId);
        request.setQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getOrder_shouldReturnOrderDTO_whenOrderExists() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .orderId(orderId)
                .quantity(3)
                .price(BigDecimal.valueOf(500))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrder(orderId);

        assertThat(dto).isNotNull();
        assertThat(dto.getOrderId()).isEqualTo(orderId);
        assertThat(dto.getQuantity()).isEqualTo(3);
    }

    @Test
    void getOrder_shouldThrow_whenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getOrders_shouldReturnPaginatedResponse() {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .quantity(1)
                .price(BigDecimal.valueOf(100))
                .build();

        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginationResponse<OrderDTO> result = orderService.getOrders(0, 10, "price");

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getPagination().getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateOrder_shouldUpdateExistingOrder() {
        UUID orderId = UUID.randomUUID();
        Order orderEntity = Order.builder().orderId(orderId).quantity(2).build();
        OrderDTO updateDto = new OrderDTO();
        updateDto.setOrderId(orderId);
        updateDto.setQuantity(10);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any())).thenReturn(orderEntity);

        OrderDTO result = orderService.updateOrder(updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(10);
    }

    @Test
    void updateOrder_shouldThrow_whenIdIsMissing() {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(null);

        assertThatThrownBy(() -> orderService.updateOrder(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteOrder_shouldCallRepositoryDelete() {
        UUID orderId = UUID.randomUUID();
        orderService.deleteOrder(orderId);
        verify(orderRepository).deleteById(orderId);
    }
}