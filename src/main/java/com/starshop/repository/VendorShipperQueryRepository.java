package com.starshop.repository;

import com.starshop.domain.Commission;
import com.starshop.domain.DeliveryAssignment;
import com.starshop.domain.Order;
import com.starshop.domain.Product;
import com.starshop.domain.Promotion;
import com.starshop.domain.Store;
import com.starshop.domain.Transaction;
import com.starshop.domain.User;
import com.starshop.domain.Voucher;
import com.starshop.domain.enums.CommonEnums.DeliveryStatus;
import com.starshop.domain.enums.OrderStatus;
import com.starshop.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Truy vấn đọc chuyên biệt cho Kênh Bán (Vendor - D4) và Giao Vận (Shipper - D5).
 * @owner: TV2 | F4.1–F4.7, F5.1–F5.3
 * Tách file riêng để không sửa các Repository gốc của Leader; sử dụng JOIN FETCH
 * để tương thích với cấu hình spring.jpa.open-in-view=false (chống N+1 và LazyInitializationException).
 */
public interface VendorShipperQueryRepository extends Repository<Store, Long> {

    // ---------- F4.1 & F4.2: Cửa hàng ----------
    @Query("select s from Store s join fetch s.owner where s.owner.id = :ownerId order by s.id asc")
    List<Store> findStoresByOwnerId(@Param("ownerId") Long ownerId);

    @Query("select s from Store s join fetch s.owner where s.id = :id and s.owner.id = :ownerId")
    Optional<Store> findStoreByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    // ---------- F4.3: Sản phẩm của Shop ----------
    @Query(value = """
           select p from Product p
           join fetch p.store
           left join fetch p.category
           where p.store.id = :storeId
           order by p.id desc
           """,
           countQuery = "select count(p) from Product p where p.store.id = :storeId")
    Page<Product> findProductsByStoreId(@Param("storeId") Long storeId, Pageable pageable);

    @Query("select p from Product p join fetch p.store left join fetch p.category where p.id = :id")
    Optional<Product> findProductWithStoreAndCategory(@Param("id") Long id);

    @Query("select count(p) from Product p where p.store.id = :storeId")
    long countProductsByStoreId(@Param("storeId") Long storeId);

    // ---------- F4.4 & F4.7: Đơn hàng của Shop ----------
    @Query("""
           select distinct o from Order o
           join fetch o.buyer
           join fetch o.store
           left join fetch o.shipper
           left join fetch o.items i
           left join fetch i.product
           where o.store.id = :storeId
           order by o.createdAt desc, o.id desc
           """)
    List<Order> findOrdersByStoreId(@Param("storeId") Long storeId);

    @Query("""
           select distinct o from Order o
           join fetch o.buyer
           join fetch o.store
           left join fetch o.shipper
           left join fetch o.items i
           left join fetch i.product
           where o.store.id = :storeId and o.status = :status
           order by o.createdAt desc, o.id desc
           """)
    List<Order> findOrdersByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status);

    @Query("""
           select distinct o from Order o
           join fetch o.buyer
           join fetch o.store
           left join fetch o.shipper
           left join fetch o.items i
           left join fetch i.product
           where o.id = :id
           """)
    Optional<Order> findOrderWithDetailsById(@Param("id") Long id);

    // ---------- F4.4, F5.1–F5.3: Shipper & Phân công giao vận ----------
    @Query("select u from User u where u.role = :role and u.enabled = true and u.locked = false order by u.fullName asc, u.id asc")
    List<User> findActiveUsersByRole(@Param("role") Role role);

    @Query("""
           select distinct a from DeliveryAssignment a
           join fetch a.order o
           join fetch o.store
           join fetch o.buyer
           left join fetch o.items i
           left join fetch i.product
           join fetch a.shipper
           where a.shipper.id = :shipperId
           order by a.assignedAt desc, a.id desc
           """)
    List<DeliveryAssignment> findAssignmentsByShipperId(@Param("shipperId") Long shipperId);

    @Query("""
           select distinct a from DeliveryAssignment a
           join fetch a.order o
           join fetch o.store s
           join fetch s.owner
           join fetch o.buyer
           left join fetch o.items i
           left join fetch i.product
           join fetch a.shipper
           where a.id = :id and a.shipper.id = :shipperId
           """)
    Optional<DeliveryAssignment> findAssignmentWithDetails(@Param("id") Long id, @Param("shipperId") Long shipperId);

    // ---------- Shipper tự nhận đơn ----------
    @Query("""
           select distinct o from Order o
           join fetch o.buyer
           join fetch o.store
           left join fetch o.items i
           left join fetch i.product
           where o.status = com.starshop.domain.enums.OrderStatus.CONFIRMED and o.shipper is null
           order by o.createdAt asc, o.id asc
           """)
    List<Order> findClaimableOrders();

    /** Khoá hàng đơn để 2 shipper không nhận trùng 1 đơn. */
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> lockOrderById(@Param("id") Long id);

    @Query("select a from DeliveryAssignment a join fetch a.order join fetch a.shipper where a.order.id = :orderId order by a.id desc")
    List<DeliveryAssignment> findAssignmentsByOrderId(@Param("orderId") Long orderId);

    @Query("""
           select count(a) from DeliveryAssignment a
           where a.shipper.id = :shipperId
             and a.status = :status
             and a.deliveredAt >= :since
           """)
    long countAssignmentsByShipperAndStatusSince(@Param("shipperId") Long shipperId,
                                                 @Param("status") DeliveryStatus status,
                                                 @Param("since") Instant since);

    // ---------- F4.5: Voucher & Khuyến mãi của Shop ----------
    @Query("select v from Voucher v join fetch v.store where v.store.id = :storeId order by v.id desc")
    List<Voucher> findVouchersByStoreId(@Param("storeId") Long storeId);

    @Query("select v from Voucher v left join fetch v.store where v.id = :id")
    Optional<Voucher> findVoucherWithStoreById(@Param("id") Long id);

    @Query("select pr from Promotion pr join fetch pr.store where pr.store.id = :storeId order by pr.id desc")
    List<Promotion> findPromotionsByStoreId(@Param("storeId") Long storeId);

    // ---------- F4.6 & F4.7: Ví Shop & Chiết khấu ----------
    @Query("select t from Transaction t join fetch t.store left join fetch t.order where t.store.id = :storeId order by t.createdAt desc, t.id desc")
    List<Transaction> findTransactionsByStoreId(@Param("storeId") Long storeId);

    @Query("select c from Commission c left join fetch c.store where c.store.id = :storeId or c.store is null order by c.id desc")
    List<Commission> findApplicableCommissions(@Param("storeId") Long storeId);
}
