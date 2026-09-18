package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Danh mục hoa (có thể phân cấp cha-con). @owner: TV3 */
@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
}
