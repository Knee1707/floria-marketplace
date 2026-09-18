package com.starshop.repository;

import com.starshop.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV2 (shop) / TV3 (hệ thống). */
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByStoreId(Long storeId);
    List<Promotion> findByStoreIsNull();
}
