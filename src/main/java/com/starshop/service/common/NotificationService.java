package com.starshop.service.common;

import com.starshop.domain.Notification;
import com.starshop.domain.enums.CommonEnums.NotificationType;

import java.util.List;

/**
 * HỢP ĐỒNG — TV2 viết NotificationServiceImpl (@owner: TV2 | F7.1).
 * notify() lưu Notification + đẩy realtime qua SimpMessagingTemplate (STOMP /user/queue/noti).
 */
public interface NotificationService {
    void notify(Long userId, NotificationType type, String content);
    List<Notification> myNotifications();
    long unreadCount(Long userId);
    void markRead(Long notificationId);
}
