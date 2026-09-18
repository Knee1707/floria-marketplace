package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.PromotionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Chương trình khuyến mãi (shop hoặc hệ thống). @owner: TV2 (shop) / TV3 (hệ thống). */
@Entity @Table(name = "promotions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Promotion extends BaseEntity {
    /** null = toàn hệ thống. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromotionType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    private Instant startAt;
    private Instant endAt;
    private Integer quota;
}
