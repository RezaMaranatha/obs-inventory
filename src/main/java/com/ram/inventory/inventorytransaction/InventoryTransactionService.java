package com.ram.inventory.inventorytransaction;

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
public class InventoryTransactionService {

	private final InventoryTransactionRepository inventoryTransactionRepository;
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	public InventoryTransactionService(InventoryTransactionRepository inventoryTransactionRepository,
			ProductRepository productRepository, ModelMapper modelMapper) {
		this.inventoryTransactionRepository = inventoryTransactionRepository;
		this.productRepository = productRepository;
		this.modelMapper = modelMapper;
	}

	@Transactional
	InventoryTransactionDTO createInventoryTransaction(InventoryTransactionRequest request) throws Exception {
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		if (request.getType().equals("W") && product.getCurrentQuantity() >= request.getQuantity()) {
			product.setCurrentQuantity(product.getCurrentQuantity() - request.getQuantity());
			InventoryTransaction transaction = this.inventoryTransactionRepository
					.save(InventoryTransaction.builder().type(InventoryTransactionType.WITHDRAW)
							.product(product).quantity(request.getQuantity()).build());
			this.productRepository.save(product);
			return modelMapper.map(transaction, InventoryTransactionDTO.class);
		} else if (request.getType().equals("T")) {
			product.setCurrentQuantity(product.getCurrentQuantity() + request.getQuantity());
			InventoryTransaction transaction = this.inventoryTransactionRepository
					.save(InventoryTransaction.builder().type(InventoryTransactionType.TOPUP)
							.product(product).quantity(request.getQuantity()).build());
			this.productRepository.save(product);
			return modelMapper.map(transaction, InventoryTransactionDTO.class);
		} else {
			throw new ResourceNotFoundException("Transaction Type missing");
		}
	}

	InventoryTransactionDTO getTransaction(UUID id) throws Exception {
		InventoryTransaction transaction = inventoryTransactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

		return modelMapper.map(transaction, InventoryTransactionDTO.class);
	}

	PaginationResponse<InventoryTransactionDTO> getTransactions(int pageNumber, int pageSize, String sortBy) {
		Streamable<Sort.Order> streamableOrders = Sort.by(Sort.Order.asc(sortBy));
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(streamableOrders.stream().toList()));

		Page<InventoryTransaction> transactions = inventoryTransactionRepository.findAll(pageable);
		List<InventoryTransactionDTO> dtoList = transactions.getContent().stream()
				.map(transaction -> modelMapper.map(transaction, InventoryTransactionDTO.class)).toList();

		return new PaginationResponse<>(
				dtoList,
				new PaginationResponse.PaginationInfo(
						transactions.getNumber(),
						transactions.getSize(),
						transactions.getTotalPages(),
						transactions.getTotalElements()));
	}

	InventoryTransactionDTO updateTransaction(InventoryTransactionDTO transaction) throws Exception {
		if (transaction.getTransactionId() == null) {
			throw new ResourceNotFoundException("Transaction ID missing");
		}

		InventoryTransaction existingTransaction = inventoryTransactionRepository
				.findById(transaction.getTransactionId())
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

		modelMapper.map(transaction, existingTransaction);
		InventoryTransaction result = inventoryTransactionRepository.save(existingTransaction);
		return modelMapper.map(result, InventoryTransactionDTO.class);
	}

	void deleteTransaction(UUID id) throws Exception {
		try {
			inventoryTransactionRepository.deleteById(id);
		} catch (Exception e) {
			throw new Exception();
		}
	}
}