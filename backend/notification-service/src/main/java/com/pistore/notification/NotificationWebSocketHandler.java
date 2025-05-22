// backend/notification-service/src/main/java/com/pistore/notification/NotificationWebSocketHandler.java
package com.pistore.notification;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class NotificationWebSocketHandler extends TextWebSocketHandler {
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Logika untuk menangani pesan dari klien (opsional)
        session.sendMessage(new TextMessage("Connected to notification service"));
    }
}
