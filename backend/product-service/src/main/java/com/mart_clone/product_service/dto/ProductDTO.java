package com.mart_clone.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String sku;
    private String category;
    private Long categoryId;
    private Boolean isActive;
    private BigDecimal rating;
    private Integer reviewCount;
    private List<String> imageUrls;
    private String primaryImage;
}
