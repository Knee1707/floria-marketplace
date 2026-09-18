package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/** Cấp độ cửa hàng + % hoa hồng. @owner: TV3. */
@Entity @Table(name = "store_levels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreLevel extends BaseEntity {
    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;
}
