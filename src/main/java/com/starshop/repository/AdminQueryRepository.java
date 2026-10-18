package com.starshop.repository;

import com.starshop.domain.Category;
import com.starshop.domain.Commission;
import com.starshop.domain.Product;
import com.starshop.domain.Promotion;
import com.starshop.domain.Store;
import com.starshop.domain.User;
import com.starshop.domain.Voucher;
import com.starshop.domain.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Truy vấn ĐỌC cho khu quản trị (Admin/Manager). @owner: TV3 | F6.1–F6.7.
 * Tách file riêng để KHÔNG sửa repository của Leader; dùng JOIN FETCH vì open-in-view=false
 * (JSP đọc store.name / parent.name / owner.email mà không bị LazyInitializationException, không N+1).
 * Tham số keyword luôn truyền dạng pattern đã lower-case, vd "%hoa%" (không truyền null → tránh lỗi kiểu tham số PostgreSQL).
 */
public interface AdminQueryRepository extends Repository<User, Long> {

    // ---------- F6.1 Người dùng ----------
    /** Admin: toàn hệ thống. roles = danh sách vai trò cần lọc (truyền đủ các role khi không lọc → tránh tham số null). */
    @Query("""
           select u from User u
           where (lower(u.email) like :kw or lower(coalesce(u.fullName, '')) like :kw or coalesce(u.phone, '') like :kw)
             and u.role in :roles
           """)
    Page<User> searchUsers(@Param("kw") String kwPattern, @Param("roles") Collection<Role> roles, Pageable pageable);

    /**
     * Manager: user "thuộc" các shop được giao = chủ shop + khách đã đặt đơn ở shop đó.
     * Dùng subquery (không JOIN) để mỗi user chỉ ra 1 dòng, phân trang đúng.
     */
    @Query("""
           select u from User u
           where (lower(u.email) like :kw or lower(coalesce(u.fullName, '')) like :kw or coalesce(u.phone, '') like :kw)
             and u.role in :roles
             and (u.id in (select s.owner.id from Store s where s.id in :storeIds)
                  or u.id in (select o.buyer.id from Order o where o.store.id in :storeIds))
           """)
    Page<User> searchUsersInStores(@Param("kw") String kwPattern, @Param("roles") Collection<Role> roles,
                                   @Param("storeIds") Collection<Long> storeIds, Pageable pageable);

    /** Chống IDOR cho Manager: user có thuộc các shop được giao không. */
    @Query("""
           select count(u) > 0 from User u
           where u.id = :userId
             and (u.id in (select s.owner.id from Store s where s.id in :storeIds)
                  or u.id in (select o.buyer.id from Order o where o.store.id in :storeIds))
           """)
    boolean isUserInStores(@Param("userId") Long userId, @Param("storeIds") Collection<Long> storeIds);

    @Query("select u from User u where u.role = :role order by lower(coalesce(u.fullName, u.email)), u.id")
    List<User> findUsersByRole(@Param("role") Role role);

    // ---------- F6.4 Cửa hàng ----------
    @Query("select s from Store s join fetch s.owner order by s.approved asc, s.id asc")
    List<Store> findAllStoresWithOwner();

    @Query("select s from Store s join fetch s.owner where s.id in :ids order by s.approved asc, s.id asc")
    List<Store> findStoresWithOwnerByIdIn(@Param("ids") Collection<Long> ids);

    // ---------- F6.4 Sản phẩm ----------
    @Query(value = """
           select p from Product p join fetch p.store left join fetch p.category
           where lower(p.name) like :kw
           """,
           countQuery = "select count(p) from Product p where lower(p.name) like :kw")
    Page<Product> searchProducts(@Param("kw") String kwPattern, Pageable pageable);

    @Query(value = """
           select p from Product p join fetch p.store left join fetch p.category
           where lower(p.name) like :kw and p.store.id in :storeIds
           """,
           countQuery = "select count(p) from Product p where lower(p.name) like :kw and p.store.id in :storeIds")
    Page<Product> searchProductsInStores(@Param("kw") String kwPattern,
                                         @Param("storeIds") Collection<Long> storeIds,
                                         Pageable pageable);

    @Query("select p from Product p join fetch p.store where p.id = :id")
    java.util.Optional<Product> findProductWithStore(@Param("id") Long id);

    // ---------- F6.2 Danh mục ----------
    @Query("select c from Category c left join fetch c.parent pr order by coalesce(pr.id, c.id), c.id")
    List<Category> findAllCategoriesWithParent();

    @Query("select count(p) from Product p where p.category.id = :categoryId")
    long countProductsByCategory(@Param("categoryId") Long categoryId);

    // ---------- F6.5 Chiết khấu ----------
    @Query("select c from Commission c left join fetch c.store order by c.id")
    List<Commission> findAllCommissionsWithStore();

    @Query("select c from Commission c join fetch c.store s where s.id in :storeIds order by c.id")
    List<Commission> findCommissionsWithStoreIn(@Param("storeIds") Collection<Long> storeIds);

    @Query("select c from Commission c left join fetch c.store where c.id = :id")
    java.util.Optional<Commission> findCommissionWithStore(@Param("id") Long id);

    // ---------- F6.6 Khuyến mãi (store = null: toàn sàn; store != null: của 1 shop) ----------
    @Query("select v from Voucher v left join fetch v.store order by v.id desc")
    List<Voucher> findAllVouchersWithStore();

    @Query("select v from Voucher v join fetch v.store s where s.id in :storeIds order by v.id desc")
    List<Voucher> findVouchersWithStoreIn(@Param("storeIds") Collection<Long> storeIds);

    @Query("select v from Voucher v left join fetch v.store where v.id = :id")
    java.util.Optional<Voucher> findVoucherWithStore(@Param("id") Long id);

    @Query("select count(v) > 0 from Voucher v where lower(v.code) = lower(:code)")
    boolean existsVoucherCode(@Param("code") String code);

    @Query("select p from Promotion p left join fetch p.store order by p.id desc")
    List<Promotion> findAllPromotionsWithStore();

    @Query("select p from Promotion p join fetch p.store s where s.id in :storeIds order by p.id desc")
    List<Promotion> findPromotionsWithStoreIn(@Param("storeIds") Collection<Long> storeIds);

    @Query("select p from Promotion p left join fetch p.store where p.id = :id")
    java.util.Optional<Promotion> findPromotionWithStore(@Param("id") Long id);
}
