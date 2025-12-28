package com.mart_clone.product_service.service;

import com.mart_clone.product_service.dto.ProductDTO;
import com.mart_clone.product_service.model.Category;
import com.mart_clone.product_service.model.Product;
import com.mart_clone.product_service.repository.CategoryRepository;
import com.mart_clone.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public Page<ProductDTO> getAllProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByIsActive(true, pageable)
            .map(this::convertToDTO);
    }
    
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryIdAndIsActive(categoryId, true, pageable)
            .map(this::convertToDTO);
    }
    
    public Page<ProductDTO> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchProducts(query, pageable)
            .map(this::convertToDTO);
    }
    
    public List<ProductDTO> getTopDeals(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopDeals(pageable)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Product product = Product.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .brand(dto.getBrand())
            .price(dto.getPrice())
            .originalPrice(dto.getOriginalPrice())
            .sku(dto.getSku())
            .category(category)
            .isActive(true)
            .rating(dto.getRating())
            .reviewCount(0)
            .build();
        
        product = productRepository.save(product);
        return convertToDTO(product);
    }
    
    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .brand(product.getBrand())
            .price(product.getPrice())
            .originalPrice(product.getOriginalPrice())
            .sku(product.getSku())
            .category(product.getCategory() != null ? product.getCategory().getName() : null)
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .isActive(product.getIsActive())
            .rating(product.getRating())
            .reviewCount(product.getReviewCount())
            .imageUrls(product.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList()))
            .primaryImage(product.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(null))
            .build();
    }
}
