package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Sản phẩm hoa của shop. @owner: TV2 */
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String thumbnailUrl;

    /** Đếm phục vụ trang chủ "bán chạy" / sắp xếp giảm dần. */
    @Column(nullable = false)
    private Long soldCount;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    /** Đếm lượt yêu thích (wishlist) để sắp xếp "yêu thích nhiều nhất". */
    @Builder.Default
    @Column(nullable = false)
    private Long favoriteCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
