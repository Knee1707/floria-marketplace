package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Cấp độ người dùng + % chiết khấu khi mua. @owner: TV3 */
@Entity
@Table(name = "user_levels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserLevel extends BaseEntity {

    @Column(nullable = false, length = 60)
    private String name;

    /** % giảm khi mua hàng (0..100). */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    /** Ngưỡng điểm/chi tiêu để đạt cấp. */
    private Long threshold;
}
