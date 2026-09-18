package com.starshop.repository;

import com.starshop.domain.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV3 dùng. */
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByStoreId(Long storeId);
}
