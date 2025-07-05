package com.ram.inventory.order;

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
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@RequestBody OrderRequest request) throws Exception {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Order Created", orderService.createOrder(request)));
	}

	@GetMapping(value = "/get-order")
	public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@RequestParam UUID id) throws Exception {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Order Found", orderService.getOrder(id)));
	}

	@GetMapping(value = "/get-orders")
	public ResponseEntity<ApiResponse<PaginationResponse<OrderDTO>>> getOrders(
			@RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "orderId") String sortBy) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Orders Found", orderService.getOrders(pageNumber, pageSize,sortBy)));
	}

	@PostMapping(value = "/update")
	public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(@RequestBody OrderDTO product) throws Exception {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Order Updated", orderService.updateOrder(product)));
	}

	@PostMapping(value = "/delete")
	public ResponseEntity<ApiResponse<String>> deleteOrder(@RequestBody Map<String,String> request) throws Exception {
		orderService.deleteOrder(UUID.fromString(request.get("id")));
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Order Deleted", "Success"));
	}
}