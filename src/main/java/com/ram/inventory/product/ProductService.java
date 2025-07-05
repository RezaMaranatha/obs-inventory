package com.ram.inventory.product;

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
import com.ram.inventory.util.PaginationResponse;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    ProductDTO createProduct(ProductDTO product) {
        Product result = productRepository.save(modelMapper.map(product, Product.class));
        return modelMapper.map(result, ProductDTO.class);
    }

    ProductDTO getProduct(UUID id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return modelMapper.map(product, ProductDTO.class);
    }

    PaginationResponse<ProductDTO> getProducts(int pageNumber, int pageSize, String sortBy) {
        Streamable<Sort.Order> streamableOrders = Sort.by(Sort.Order.asc(sortBy)).and(Sort.Order.asc("productId"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(streamableOrders.stream().toList()));

        Page<Product> products = productRepository.findAll(pageable);
        List<ProductDTO> dtoList = products.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        return new PaginationResponse<>(
                dtoList,
                new PaginationResponse.PaginationInfo(
                        products.getNumber(),
                        products.getSize(),
                        products.getTotalPages(),
                        products.getTotalElements()));
    }

    ProductDTO updateProduct(ProductDTO product){
        if (product.getProductId() == null) {
            throw new ResourceNotFoundException("Product ID missing");
        }

        Product existingProduct = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        modelMapper.map(product, existingProduct);
        Product result = productRepository.save(existingProduct);
        return modelMapper.map(result, ProductDTO.class);
    }

    void deleteProduct(UUID id){
        productRepository.deleteById(id);
    }
}