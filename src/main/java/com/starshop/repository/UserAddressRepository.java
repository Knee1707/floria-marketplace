package com.starshop.repository;

import com.starshop.domain.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: L. */
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
}
