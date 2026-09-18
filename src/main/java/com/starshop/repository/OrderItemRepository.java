package com.starshop.repository;

import com.starshop.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/** @owner: L. Kiểm "đã mua" để cho phép đánh giá (F3.17). */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByOrderBuyerIdAndProductId(Long buyerId, Long productId);
}
