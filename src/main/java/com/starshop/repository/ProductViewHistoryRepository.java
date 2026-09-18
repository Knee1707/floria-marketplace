package com.starshop.repository;

import com.starshop.domain.ProductViewHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** @owner: L. */
public interface ProductViewHistoryRepository extends JpaRepository<ProductViewHistory, Long> {
    @EntityGraph(attributePaths = {"product", "product.store"})
    List<ProductViewHistory> findTop20ByUserIdOrderByViewedAtDesc(Long userId);
    Optional<ProductViewHistory> findByUserIdAndProductId(Long userId, Long productId);
}
