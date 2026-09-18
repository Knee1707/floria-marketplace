package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Sản phẩm đã xem — F3.16. @owner: L. Bảng product_view_history (V1). */
@Entity @Table(name = "product_view_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductViewHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;
}
