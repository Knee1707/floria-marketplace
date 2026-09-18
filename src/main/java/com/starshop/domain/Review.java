package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Đánh giá + bình luận (text >= 50 ký tự, kèm media). Chỉ SP đã mua. @owner: TV1 */
@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 1..5 sao. */
    @Column(nullable = false)
    private Integer rating;

    /** Bình luận tối thiểu 50 ký tự (validate ở tầng service/DTO). */
    @Column(nullable = false, columnDefinition = "text")
    private String comment;
}
