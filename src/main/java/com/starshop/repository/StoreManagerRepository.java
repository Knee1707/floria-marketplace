package com.starshop.repository;

import com.starshop.domain.StoreManager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV3 dùng. */
public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    List<StoreManager> findByManagerId(Long managerId);
}
