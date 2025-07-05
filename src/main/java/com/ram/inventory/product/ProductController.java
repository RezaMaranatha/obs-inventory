package com.ram.inventory.product;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ram.inventory.util.ApiResponse;
import com.ram.inventory.util.PaginationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductDTO product) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Product Created", productService.createProduct(product)));
	}

	@GetMapping(value = "/get-product")
	public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@RequestParam UUID id) throws Exception {
		return ResponseEntity.ok(new ApiResponse<>(200, "Product Found", productService.getProduct(id)));

	}

	@GetMapping(value = "/get-products")
	public ResponseEntity<ApiResponse<PaginationResponse<ProductDTO>>> getProducts(
			@RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "name") String sortBy) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Products Found", productService.getProducts(pageNumber, pageSize, sortBy)));
	}

	@PostMapping(value = "/update")
	public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@RequestBody ProductDTO product) {
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Product Updated", productService.updateProduct(product)));
	}

	@PostMapping(value = "/delete")
	public ResponseEntity<ApiResponse<String>> deleteProduct(@RequestBody Map<String, String> request) {
		productService.deleteProduct(UUID.fromString(request.get("id")));
		return ResponseEntity
				.ok(new ApiResponse<>(200, "Product Deleted", "Success"));
	}
}