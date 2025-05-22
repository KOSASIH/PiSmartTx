// backend/notification-service/src/main/java/com/pistore/notification/NotificationService.java
package com.pistore.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class NotificationService {
    @Autowired
    private WebSocketSessionManager sessionManager;

    public void sendNotification(String userId, String message) throws Exception {
        WebSocketSession session = sessionManager.getSession(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
