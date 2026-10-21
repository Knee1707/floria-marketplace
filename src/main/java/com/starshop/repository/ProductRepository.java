package com.starshop.repository;

import com.starshop.domain.Product;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @owner: TV2. Dùng @EntityGraph để nạp store/category cùng lúc, tránh N+1
 * (open-in-view=false — xem rules/database khuyến nghị chuan-nganh).
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Trang chủ: top SP đang bán, sắp xếp bán chạy giảm dần. */
    @EntityGraph(attributePaths = {"store"})
    List<Product> findTop12ByStatusOrderBySoldCountDesc(ProductStatus status);

    /** Tìm kiếm theo tên (F3.2) có phân trang (F3.7). */
    @EntityGraph(attributePaths = {"store", "category"})
    Page<Product> findByStatusAndNameContainingIgnoreCase(ProductStatus status, String name, Pageable pageable);

    /** SP theo danh mục (F3.5). */
    @EntityGraph(attributePaths = {"store", "category"})
    Page<Product> findByStatusAndCategoryId(ProductStatus status, Long categoryId, Pageable pageable);

    /** Chi tiết SP: nạp kèm store + category. */
    @EntityGraph(attributePaths = {"store", "category"})
    Optional<Product> findWithStoreById(Long id);

    /** SP của 1 shop (Vendor). @owner dùng. */
    Page<Product> findByStoreId(Long storeId, Pageable pageable);

    /**
     * Tìm kiếm + lọc SP cho trang /products (F3.2–F3.7): theo từ khoá, danh mục, khoảng giá.
     * Cách SẮP XẾP (mới / bán chạy / đánh giá / yêu thích) truyền qua Sort của Pageable
     * (sort theo cột createdAt / soldCount / ratingAvg / favoriteCount).
     */
    @EntityGraph(attributePaths = {"store", "category"})
    @Query("""
            select p from Product p
            where p.status = :status
              and (:categoryId is null or p.category.id = :categoryId)
              and (:keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%')))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("status") ProductStatus status,
                         @Param("categoryId") Long categoryId,
                         @Param("keyword") String keyword,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         Pageable pageable);
}
