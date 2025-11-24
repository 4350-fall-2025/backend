package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.message.OnlineVetMessage;
import com.softeng.backend.services.socket.OnlineVetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class OnlineVetController {

    private static final Logger logger = LoggerFactory.getLogger(OnlineVetController.class);
    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public OnlineVetController(OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @MessageMapping("/vet/online")
    public void Online(OnlineVetMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userId = msg != null ? msg.getUserId() : null;
        if (userId == null || userId.isBlank()) return;
        onlineVetService.addUserIds(sessionId, userId);
        template.convertAndSend("/topic/online", onlineVetService.getOnlineUserIds());

        logger.info("User {} has been registered successfully", userId);
    }
}
