package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Nhà vận chuyển (tên + phí ship). @owner: TV3. */
@Entity @Table(name = "deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery extends BaseEntity {
    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee;
}
