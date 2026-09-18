package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/** Mã giảm giá / khuyến mãi (shop hoặc hệ thống). @owner: TV2 (shop), TV3 (hệ thống). */
@Entity
@Table(name = "vouchers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voucher extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromotionType type;

    /** Giá trị giảm: nếu type PRODUCT_PERCENT là %, nếu SHIPPING là số tiền phí ship được giảm. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(precision = 12, scale = 2)
    private BigDecimal minOrder;

    @Column(precision = 12, scale = 2)
    private BigDecimal maxDiscount;

    private Integer quota;
    private Integer usedCount;

    private Instant startAt;
    private Instant endAt;

    /** null = voucher toàn hệ thống; có store = voucher của shop. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    private boolean active;
}
