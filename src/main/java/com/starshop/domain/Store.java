package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Cửa hàng (shop) trong chuỗi. @owner: TV2 */
@Entity
@Table(name = "stores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Store extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String avatarUrl;
    private String coverUrl;

    /** Vendor sở hữu shop. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /** true = được duyệt kinh doanh; false = bị cấm/chờ duyệt. */
    @Column(nullable = false)
    private boolean approved;
}
