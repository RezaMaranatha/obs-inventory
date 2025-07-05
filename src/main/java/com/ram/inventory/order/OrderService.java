package com.ram.inventory.order;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ram.inventory.exception.ResourceNotFoundException;
import com.ram.inventory.product.Product;
import com.ram.inventory.product.ProductRepository;
import com.ram.inventory.util.PaginationResponse;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    OrderDTO createOrder(OrderRequest request) throws Exception {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getCurrentQuantity() < request.getQuantity()) {
            throw new Exception();
        }
        product.setCurrentQuantity(product.getCurrentQuantity() - request.getQuantity());
        Order order = this.orderRepository
                .save(Order.builder().product(product).quantity(request.getQuantity()).price(product.getPrice())
                        .build());
        this.productRepository.save(product);
        return modelMapper.map(order, OrderDTO.class);
    }

    OrderDTO getOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return modelMapper.map(order, OrderDTO.class);
    }

    PaginationResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy) {
        Streamable<Sort.Order> streamableOrders = Sort.by(Sort.Order.asc(sortBy));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(streamableOrders.stream().toList()));

        Page<Order> orders = orderRepository.findAll(pageable);
        List<OrderDTO> dtoList = orders.getContent().stream()
                .map(product -> modelMapper.map(product, OrderDTO.class)).toList();

        return new PaginationResponse<>(
                dtoList,
                new PaginationResponse.PaginationInfo(
                        orders.getNumber(),
                        orders.getSize(),
                        orders.getTotalPages(),
                        orders.getTotalElements()));
    }

    OrderDTO updateOrder(OrderDTO order) {
        if (order.getOrderId() == null) {
            throw new ResourceNotFoundException("Order ID missing");
        }

        Order existingProduct = orderRepository.findById(order.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        modelMapper.map(order, existingProduct);
        Order result = orderRepository.save(existingProduct);
        return modelMapper.map(result, OrderDTO.class);
    }

    void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }
}