package com.starshop.domain;

import jakarta.persistence.*;
import lombok.*;

/** Địa chỉ nhận hàng của user (1 user nhiều địa chỉ). @owner: L.
 *  Tách bảng theo LUẬT chống phình (rules/database/02). Bảng: V3__user_address.sql. */
@Entity @Table(name = "user_addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAddress extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 120)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 300)
    private String addressLine;

    private String ward;
    private String district;
    private String province;

    @Column(nullable = false)
    private boolean isDefault;
}
