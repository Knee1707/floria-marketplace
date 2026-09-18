package com.starshop.repository;

import com.starshop.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV2 dùng. */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}
