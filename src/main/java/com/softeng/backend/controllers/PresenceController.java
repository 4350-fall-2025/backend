package com.softeng.backend.controllers;

import com.softeng.backend.models.RegisterMessage;
import com.softeng.backend.services.user.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PresenceController {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate template;

    @Autowired
    public PresenceController(OnlineUserService onlineUserService, SimpMessagingTemplate template) {
        this.onlineUserService = onlineUserService;
        this.template = template;
    }

    @MessageMapping("/online/register")
    public void register(RegisterMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String username = msg != null ? msg.getUsername() : null;
        if (username == null || username.isBlank()) return;
        onlineUserService.addUser(sessionId, username);
        template.convertAndSend("/topic/online", onlineUserService.getOnlineUsers());
    }
}
