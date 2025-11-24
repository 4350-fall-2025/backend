package com.softeng.backend.listener;

import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public interface IStompSubscribeListener {
    void handleSessionSubscribe(SessionSubscribeEvent event);
}
