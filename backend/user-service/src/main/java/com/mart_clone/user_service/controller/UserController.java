package com.mart_clone.user_service.controller;

import com.mart_clone.user_service.dto.UserAddressDTO;
import com.mart_clone.user_service.dto.UserProfileDTO;
import com.mart_clone.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
    
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<UserAddressDTO>> getAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getAddressesByUserId(userId));
    }
    
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<UserAddressDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody UserAddressDTO dto) {
        return ResponseEntity.ok(userService.addAddress(userId, dto));
    }
    
    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<UserAddressDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody UserAddressDTO dto) {
        return ResponseEntity.ok(userService.updateAddress(userId, addressId, dto));
    }
    
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        userService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}