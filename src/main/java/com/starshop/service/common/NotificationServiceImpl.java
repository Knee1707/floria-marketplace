package com.starshop.service.common;

import com.starshop.domain.Notification;
import com.starshop.domain.User;
import com.starshop.domain.enums.CommonEnums.NotificationType;
import com.starshop.repository.NotificationRepository;
import com.starshop.repository.UserRepository;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Triển khai dịch vụ Thông báo trong ứng dụng kết hợp đẩy Realtime qua WebSocket STOMP.
 * @owner: TV2 | @leaf: F7.1 WebSocket thông báo | @status: DONE
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void notify(Long userId, NotificationType type, String content) {
        if (userId == null || content == null || content.isBlank()) {
            return;
        }
        User receiver = userRepository.findById(userId).orElse(null);
        if (receiver == null) {
            return;
        }

        Notification saved = notificationRepository.save(Notification.builder()
                .user(receiver)
                .type(type != null ? type : NotificationType.SYSTEM)
                .content(content.trim())
                .read(false)
                .build());

        Map<String, Object> payload = Map.of(
                "id", saved.getId(),
                "userId", receiver.getId(),
                "type", saved.getType().name(),
                "content", saved.getContent(),
                "read", saved.isRead(),
                "createdAt", (saved.getCreatedAt() != null ? saved.getCreatedAt() : Instant.now()).toString()
        );

        try {
            // Đẩy qua user principal (email trong Spring Security) và userId theo hợp đồng STOMP /user/queue/noti
            messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/noti", payload);
            messagingTemplate.convertAndSendToUser(String.valueOf(receiver.getId()), "/queue/noti", payload);
        } catch (Exception ex) {
            log.debug("[F7.1 WebSocket] Không thể đẩy STOMP realtime cho user {}: {}", userId, ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> myNotifications() {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.id());
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        if (userId == null) {
            return 0L;
        }
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markRead(Long notificationId) {
        if (notificationId == null) {
            return;
        }
        Long myId = currentUser.id();
        notificationRepository.findById(notificationId).ifPresent(noti -> {
            // Chống IDOR: chỉ cho phép đánh dấu đã đọc thông báo của chính mình
            if (noti.getUser() != null && noti.getUser().getId().equals(myId)) {
                noti.setRead(true);
                notificationRepository.save(noti);
            }
        });
    }
}
