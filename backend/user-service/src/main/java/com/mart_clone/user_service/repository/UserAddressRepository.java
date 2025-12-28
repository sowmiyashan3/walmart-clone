package com.mart_clone.user_service.repository;

import com.mart_clone.user_service.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);
    List<UserAddress> findByUserIdOrderByIsDefaultDesc(Long userId);
}