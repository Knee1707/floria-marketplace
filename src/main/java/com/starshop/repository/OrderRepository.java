package com.starshop.repository;

import com.starshop.domain.Order;
import com.starshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/** @owner: L tạo; TV2 dùng cho đơn shop. */
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    @EntityGraph(attributePaths = {"store"})
    Page<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status, Pageable pageable);

    /** Chi tiết đơn: nạp kèm store, shipper, items + sản phẩm (tránh LazyInit khi render JSP, open-in-view=false). */
    @EntityGraph(attributePaths = {"store", "shipper", "items", "items.product"})
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findDetailById(@Param("id") Long id);

    List<Order> findByStoreId(Long storeId);
    List<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status);
    long countByStatus(OrderStatus status);
}
