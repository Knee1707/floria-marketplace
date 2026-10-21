package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.ReviewTarget;
import jakarta.persistence.*;
import lombok.*;

/**
 * Đánh giá sau khi đơn GIAO THÀNH CÔNG: khách chấm điểm CỬA HÀNG và SHIPPER của đơn.
 * Mỗi đơn chỉ 1 đánh giá cho mỗi đối tượng (unique order+user+target).
 * Đối tượng cụ thể (shop/shipper) suy từ order.store / order.shipper.
 * @owner: TV1
 */
@Entity
@Table(name = "order_reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "user_id", "target"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Người đánh giá (chủ đơn). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReviewTarget target;

    /** 1..5 sao. */
    @Column(nullable = false)
    private Integer rating;

    /** Nhận xét (tuỳ chọn). */
    @Column(columnDefinition = "text")
    private String comment;
}
