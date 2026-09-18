package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Manager phụ trách shop nào (Manager = quản lý cửa hàng được phân công). @owner: TV3. */
@Entity @Table(name = "store_managers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreManager extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;
}
