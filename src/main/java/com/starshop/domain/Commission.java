package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Chính sách chiết khấu (hoa hồng) app thu của shop. @owner: TV3. */
@Entity @Table(name = "commissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commission extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @Column(length = 255)
    private String note;
}
