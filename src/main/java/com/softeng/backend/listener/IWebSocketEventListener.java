package com.softeng.backend.listener;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public interface IWebSocketEventListener {
    void handleSessionDisconnect(SessionDisconnectEvent event);
}
