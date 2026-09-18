package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Sản phẩm yêu thích (wishlist) — F3.15. @owner: L. Bảng user_follow_product (V1). */
@Entity
@Table(name = "user_follow_product", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserFollowProduct extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
