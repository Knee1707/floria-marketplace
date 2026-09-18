package com.starshop.repository;

import com.starshop.domain.UserFollowProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: L. */
public interface UserFollowProductRepository extends JpaRepository<UserFollowProduct, Long> {
    @EntityGraph(attributePaths = {"product", "product.store"})
    List<UserFollowProduct> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
