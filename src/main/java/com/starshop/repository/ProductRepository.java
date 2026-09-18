package com.starshop.repository;

import com.starshop.domain.Product;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
