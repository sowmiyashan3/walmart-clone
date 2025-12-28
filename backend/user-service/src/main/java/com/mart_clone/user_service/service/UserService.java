package com.mart_clone.user_service.service;

import com.mart_clone.user_service.dto.UserAddressDTO;
import com.mart_clone.user_service.dto.UserProfileDTO;
import com.mart_clone.user_service.model.UserAddress;
import com.mart_clone.user_service.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserAddressRepository addressRepository;
    private final RestTemplate restTemplate;
    
    public UserProfileDTO getUserProfile(Long userId) {
        // Get user info from auth service
        String authServiceUrl = "http://auth-service/api/auth/user/" + userId;
        UserProfileDTO profile = restTemplate.getForObject(authServiceUrl, UserProfileDTO.class);
        
        // Add addresses
        List<UserAddressDTO> addresses = getAddressesByUserId(userId);
        profile.setAddresses(addresses);
        
        return profile;
    }
    
    public List<UserAddressDTO> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public UserAddressDTO addAddress(Long userId, UserAddressDTO dto) {
        // If this is set as default, unset other defaults
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            addressRepository.findByUserId(userId).forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }
        
        UserAddress address = UserAddress.builder()
            .userId(userId)
            .addressLine1(dto.getAddressLine1())
            .addressLine2(dto.getAddressLine2())
            .city(dto.getCity())
            .state(dto.getState())
            .zipCode(dto.getZipCode())
            .country(dto.getCountry())
            .isDefault(dto.getIsDefault())
            .build();
        
        address = addressRepository.save(address);
        return convertToDTO(address);
    }
    
    @Transactional
    public UserAddressDTO updateAddress(Long userId, Long addressId, UserAddressDTO dto) {
        UserAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            addressRepository.findByUserId(userId).forEach(addr -> {
                if (!addr.getId().equals(addressId)) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            });
        }
        
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setIsDefault(dto.getIsDefault());
        
        address = addressRepository.save(address);
        return convertToDTO(address);
    }
    
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        addressRepository.delete(address);
    }
    
    private UserAddressDTO convertToDTO(UserAddress address) {
        return UserAddressDTO.builder()
            .id(address.getId())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(address.getZipCode())
            .country(address.getCountry())
            .isDefault(address.getIsDefault())
            .build();
    }
}