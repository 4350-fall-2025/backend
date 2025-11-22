package com.softeng.backend.listener;

import com.softeng.backend.services.user.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketEventListener(OnlineUserService onlineUserService, SimpMessagingTemplate template) {
        this.onlineUserService = onlineUserService;
        this.template = template;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        onlineUserService.removeSession(sessionId);
        template.convertAndSend("/topic/online", onlineUserService.getOnlineUsers());
    }
}
