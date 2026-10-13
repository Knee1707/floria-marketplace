package com.starshop.repository;

import com.starshop.domain.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** @owner: L. */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"product", "product.store"})
    List<CartItem> findByCartId(Long cartId);
}
