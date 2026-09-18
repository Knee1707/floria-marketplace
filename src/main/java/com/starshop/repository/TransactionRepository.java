package com.starshop.repository;

import com.starshop.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV2 dùng. */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStoreId(Long storeId);
}
