package com.mart_clone.order_service.service;

import com.mart_clone.order_service.dto.CreateOrderRequest;
import com.mart_clone.order_service.dto.OrderDTO;
import com.mart_clone.order_service.dto.OrderItemDTO;
import com.mart_clone.order_service.model.Order;
import com.mart_clone.order_service.model.OrderItem;
import com.mart_clone.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        BigDecimal total = request.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order order = orderRepository.save(
            Order.builder()
                .userId(request.getUserId())
                .orderStatus(Order.OrderStatus.PENDING)
                .totalAmount(total)
                .shippingAddressId(request.getShippingAddressId())
                .build()
        );

        List<OrderItem> items = request.getItems().stream()
            .map(itemReq -> OrderItem.builder()
                .order(order)   // ✅ now effectively final
                .productId(itemReq.getProductId())
                .quantity(itemReq.getQuantity())
                .price(itemReq.getPrice())
                .build())
            .collect(Collectors.toList());

        order.setItems(items);
        
        log.info("Created order {} for user {}", order.getId(), order.getUserId());
        
        return convertToDTO(order);
    }
    
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public OrderDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setOrderStatus(status);
        order = orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
            .map(item -> OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build())
            .collect(Collectors.toList());
        
        return OrderDTO.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .orderStatus(order.getOrderStatus().name())
            .totalAmount(order.getTotalAmount())
            .createdAt(order.getCreatedAt())
            .items(items)
            .build();
    }
}