package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Dòng sản phẩm trong đơn (chốt giá tại thời điểm mua). @owner: TV1 */
@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    /** Giá tại thời điểm mua (không đổi theo giá SP sau này). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
