package com.starshop.repository;

import com.starshop.domain.Order;
import com.starshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: L tạo; TV2 dùng cho đơn shop. */
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    Page<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status, Pageable pageable);

    List<Order> findByStoreId(Long storeId);
    List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status);
    long countByStatus(OrderStatus status);
}
