package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.services.socket.OnlineVetService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class OnlineVetController{

    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public OnlineVetController(OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @MessageMapping("/vet/online")
    public void Online(@NotNull SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (sessionId == null || user == null) {
            log.warn("Session ID or user is null");
            return;
        }

        String vetId = headerAccessor.getUser().getName();
        if (vetId == null) {
            log.warn("No vetId found for session {}", sessionId);
            return;
        }

        onlineVetService.addVetIds(sessionId, vetId);
        template.convertAndSend("/topic/online", onlineVetService.getOnlineVetIds());

        log.info("User {} is online", vetId);
    }
}
