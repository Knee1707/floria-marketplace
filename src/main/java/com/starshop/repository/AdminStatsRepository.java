package com.starshop.repository;

import com.starshop.domain.Order;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Truy vấn THỐNG KÊ cho dashboard (F6.8–F6.10). @owner: TV3.
 * Metric theo rules/database/05-metrics-media.md:
 *   - Doanh thu = tổng orders.total của đơn DELIVERED (1 định nghĩa, dùng chung Admin & Manager).
 * Mỗi truy vấn có 2 bản: toàn hệ thống (Admin) và giới hạn store_id (Manager).
 * Chuỗi theo ngày dùng native SQL (PostgreSQL) để gom nhóm theo ngày giờ Việt Nam.
 */
public interface AdminStatsRepository extends Repository<Order, Long> {

    // ---------- KPI ----------
    @Query("select coalesce(sum(o.total), 0) from Order o where o.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") OrderStatus status);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.status = :status and o.store.id in :storeIds")
    BigDecimal sumTotalByStatusInStores(@Param("status") OrderStatus status,
                                        @Param("storeIds") Collection<Long> storeIds);

    @Query("select count(o) from Order o where o.createdAt >= :from")
    long countOrdersSince(@Param("from") Instant from);

    @Query("select count(o) from Order o where o.createdAt >= :from and o.store.id in :storeIds")
    long countOrdersSinceInStores(@Param("from") Instant from, @Param("storeIds") Collection<Long> storeIds);

    @Query("select count(s) from Store s where s.approved = false")
    long countPendingStores();

    @Query("select count(p) from Product p where p.store.id in :storeIds and p.status = :status")
    long countProductsInStoresByStatus(@Param("storeIds") Collection<Long> storeIds,
                                       @Param("status") ProductStatus status);

    // ---------- Phân bố trạng thái đơn (biểu đồ tròn) ----------
    @Query("select o.status, count(o) from Order o where o.createdAt >= :from group by o.status")
    List<Object[]> countByStatusSince(@Param("from") Instant from);

    @Query("select o.status, count(o) from Order o where o.createdAt >= :from and o.store.id in :storeIds group by o.status")
    List<Object[]> countByStatusSinceInStores(@Param("from") Instant from, @Param("storeIds") Collection<Long> storeIds);

    // ---------- Top cửa hàng theo doanh thu ----------
    @Query("""
           select s.id, s.name, coalesce(sum(o.total), 0), count(o)
           from Order o join o.store s
           where o.status = :status and o.createdAt >= :from
           group by s.id, s.name order by coalesce(sum(o.total), 0) desc
           """)
    List<Object[]> topStoresSince(@Param("status") OrderStatus status, @Param("from") Instant from, Pageable pageable);

    @Query("""
           select s.id, s.name, coalesce(sum(o.total), 0), count(o)
           from Order o join o.store s
           where o.status = :status and o.createdAt >= :from
             and s.id in :storeIds
           group by s.id, s.name order by coalesce(sum(o.total), 0) desc
           """)
    List<Object[]> topStoresSinceInStores(@Param("status") OrderStatus status, @Param("from") Instant from,
                                          @Param("storeIds") Collection<Long> storeIds, Pageable pageable);

    // ---------- Chuỗi theo ngày: [ngày 'YYYY-MM-DD', doanh thu đơn đã giao, số đơn] ----------
    @Query(value = """
           select to_char(o.created_at at time zone 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') as d,
                  coalesce(sum(o.total) filter (where o.status = 'DELIVERED'), 0) as revenue,
                  count(*) as orders
           from orders o
           where o.created_at >= :from
           group by 1 order by 1
           """, nativeQuery = true)
    List<Object[]> dailyOrders(@Param("from") Instant from);

    @Query(value = """
           select to_char(o.created_at at time zone 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') as d,
                  coalesce(sum(o.total) filter (where o.status = 'DELIVERED'), 0) as revenue,
                  count(*) as orders
           from orders o
           where o.created_at >= :from and o.store_id in (:storeIds)
           group by 1 order by 1
           """, nativeQuery = true)
    List<Object[]> dailyOrdersInStores(@Param("from") Instant from, @Param("storeIds") Collection<Long> storeIds);

    // ---------- User mới / shop mới theo ngày (chỉ Admin) ----------
    @Query(value = """
           select to_char(u.created_at at time zone 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') as d, count(*)
           from users u where u.created_at >= :from group by 1 order by 1
           """, nativeQuery = true)
    List<Object[]> dailyNewUsers(@Param("from") Instant from);

    @Query(value = """
           select to_char(s.created_at at time zone 'Asia/Ho_Chi_Minh', 'YYYY-MM-DD') as d, count(*)
           from stores s where s.created_at >= :from group by 1 order by 1
           """, nativeQuery = true)
    List<Object[]> dailyNewStores(@Param("from") Instant from);
}
