package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Giá trị của một thuộc tính (vd Style "Màu" -> "Đỏ","Hồng"). @owner: TV3. */
@Entity @Table(name = "style_values")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StyleValue extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    @Column(nullable = false, length = 80)
    private String value;
}
