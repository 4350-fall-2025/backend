package com.softeng.backend.listener;

import com.softeng.backend.services.socket.OnlineVetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketEventListener(OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        onlineVetService.removeSession(sessionId);
        template.convertAndSend("/topic/online", onlineVetService.getOnlineUserIds());
    }
}
