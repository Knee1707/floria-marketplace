package com.starshop.controller.vendor;

import com.starshop.domain.enums.CommonEnums.NotificationType;
import com.starshop.service.common.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;

/**
 * STOMP WebSocket Controller xử lý tin nhắn chat realtime (/app/chat).
 * @owner: TV2 | @leaf: F7.2 Chat realtime | @status: DONE
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @MessageMapping("/chat")
    public void handleChat(@Payload ChatPayload message, Principal principal) {
        if (message == null || message.getContent() == null || message.getContent().isBlank()) {
            return;
        }
        String sender = principal != null ? principal.getName()
                : (message.getSender() != null ? message.getSender() : "Khách");
        String cleanContent = message.getContent().trim();

        Map<String, Object> outbound = Map.of(
                "sender", sender,
                "receiverId", message.getReceiverId() != null ? message.getReceiverId() : 0L,
                "content", cleanContent,
                "sentAt", Instant.now().toString()
        );

        // Phát lên kênh chung /topic/chat và kênh riêng của người nhận (nếu có receiverId)
        messagingTemplate.convertAndSend("/topic/chat", outbound);
        if (message.getReceiverId() != null) {
            notificationService.notify(
                    message.getReceiverId(),
                    NotificationType.CHAT,
                    "Tin nhắn mới từ " + sender + ": " + cleanContent
            );
        }
    }

    @Data
    public static class ChatPayload {
        private Long receiverId;
        private String sender;
        private String content;
    }
}
