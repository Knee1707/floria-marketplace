package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Phân công đơn cho shipper. @owner: TV2 (shipper). */
@Entity @Table(name = "delivery_assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryAssignment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private User shipper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    private Instant assignedAt;
    private Instant deliveredAt;
}
