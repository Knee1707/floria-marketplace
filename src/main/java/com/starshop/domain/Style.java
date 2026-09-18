package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Thuộc tính sản phẩm (màu, dịp, kích thước...). @owner: TV3 (nghiệp vụ). */
@Entity @Table(name = "styles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Style extends BaseEntity {
    @Column(nullable = false, length = 80)
    private String name;
}
