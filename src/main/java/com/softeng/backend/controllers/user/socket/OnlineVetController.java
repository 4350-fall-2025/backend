package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.services.socket.OnlineVetService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class OnlineVetController implements IOnlineVetController{

    private static final Logger logger = LoggerFactory.getLogger(OnlineVetController.class);
    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public OnlineVetController(OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @Override
    @MessageMapping("/vet/online")
    public void Online(@NotNull SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (sessionId == null || user == null) {
            logger.warn("Session ID or user is null");
            return;
        }

        String vetId = headerAccessor.getUser().getName();
        if (vetId == null) {
            logger.warn("No vetId found for session {}", sessionId);
            return;
        }

        onlineVetService.addVetIds(sessionId, vetId);
        template.convertAndSend("/topic/online", onlineVetService.getOnlineVetIds());

        logger.info("User {} is online", vetId);
    }
}
