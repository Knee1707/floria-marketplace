package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Giao dịch ví shop (bán, rút, hoa hồng, hoàn). @owner: TV2. */
@Entity @Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String note;
}
