package com.mart_clone.cart_service.service;

import com.mart_clone.cart_service.dto.AddToCartRequest;
import com.mart_clone.cart_service.dto.CartDTO;
import com.mart_clone.cart_service.dto.CartItemDTO;
import com.mart_clone.cart_service.dto.ProductDTO;
import com.mart_clone.cart_service.model.Cart;
import com.mart_clone.cart_service.model.CartItem;
import com.mart_clone.cart_service.repository.CartItemRepository;
import com.mart_clone.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate;
    
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
            .orElseGet(() -> createNewCart(userId));
        
        return convertToDTO(cart);
    }
    
    @Transactional
    public CartDTO addToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
            .orElseGet(() -> createNewCart(userId));
        
        // Get product details
        String productUrl = "http://product-service/api/products/" + request.getProductId();
        ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);
        
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Check if item already in cart
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
            .orElse(null);
        
        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            item = CartItem.builder()
                .cart(cart)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .priceAtTime(product.getPrice())
                .build();
            cart.getItems().add(item);
        }
        
        cartItemRepository.save(item);
        cart = cartRepository.save(cart);
        
        return convertToDTO(cart);
    }
    
    @Transactional
    public CartDTO updateCartItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        
        return convertToDTO(cart);
    }
    
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        cartItemRepository.delete(item);
        cart.getItems().remove(item);
    }
    
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
            .userId(userId)
            .status(Cart.CartStatus.ACTIVE)
            .build();
        return cartRepository.save(cart);
    }
    
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
            .map(this::convertItemToDTO)
            .collect(Collectors.toList());
        
        BigDecimal total = items.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer itemCount = items.stream()
            .mapToInt(CartItemDTO::getQuantity)
            .sum();
        
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .items(items)
            .total(total)
            .itemCount(itemCount)
            .build();
    }
    
    private CartItemDTO convertItemToDTO(CartItem item) {
        // Get product details
        String productUrl = "http://product-service/api/products/" + item.getProductId();
        ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);
        
        BigDecimal subtotal = item.getPriceAtTime()
            .multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return CartItemDTO.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .productName(product != null ? product.getName() : "Unknown")
            .productImage(product != null ? product.getPrimaryImage() : null)
            .quantity(item.getQuantity())
            .price(item.getPriceAtTime())
            .subtotal(subtotal)
            .build();
    }
}