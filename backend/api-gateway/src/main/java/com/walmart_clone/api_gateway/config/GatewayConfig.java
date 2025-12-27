package com.walmart_clone.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Auth Service Routes
            .route("auth-service", r -> r
                .path("/api/auth/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://auth-service"))
            
            // User Service Routes
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://user-service"))
            
            // Product Service Routes
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://product-service"))
            
            // Cart Service Routes
            .route("cart-service", r -> r
                .path("/api/cart/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://cart-service"))
            
            // Order Service Routes
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://order-service"))
            
            // Payment Service Routes
            .route("payment-service", r -> r
                .path("/api/payments/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://payment-service"))
            
            // Inventory Service Routes
            .route("inventory-service", r -> r
                .path("/api/inventory/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://inventory-service"))
            
            // Admin Service Routes
            .route("admin-service", r -> r
                .path("/api/admin/**")
                .filters(f -> f
                    .stripPrefix(0)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://admin-service"))
            
            .build();
    }
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}