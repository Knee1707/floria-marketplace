package com.starshop.repository;

import com.starshop.domain.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** @owner: L (apply) / TV2 (shop) / TV3 (hệ thống). */
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCodeAndActiveTrue(String code);
    List<Voucher> findByStoreId(Long storeId);
    List<Voucher> findByStoreIsNull();
}
