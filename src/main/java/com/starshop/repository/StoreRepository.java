package com.starshop.repository;

import com.starshop.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** @owner: TV2 dùng. Leader tạo. */
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwnerId(Long ownerId);
    Optional<Store> findByIdAndOwnerId(Long id, Long ownerId);
    List<Store> findByApprovedTrue();
}
