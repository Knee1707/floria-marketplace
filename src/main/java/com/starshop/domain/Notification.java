package com.starshop.domain;

import com.starshop.domain.enums.CommonEnums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

/** Thông báo trong app (WebSocket realtime). @owner: TV2. */
@Entity @Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean read;
}
