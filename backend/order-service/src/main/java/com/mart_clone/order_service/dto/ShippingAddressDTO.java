package com.mart_clone.order_service.dto;

import lombok.Data;

@Data
public class ShippingAddressDTO {
    private String addressLine1;
    private String city;
    private String state;
    private String zipCode;
}
