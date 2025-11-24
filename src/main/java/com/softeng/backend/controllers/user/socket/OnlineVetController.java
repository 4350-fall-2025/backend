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
    public void Online(OnlineVetMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String vetId = msg.getVetId();

        onlineVetService.addVetIds(sessionId, vetId);
        template.convertAndSend("/topic/online", onlineVetService.getOnlineVetIds());

        logger.info("User {} has been registered successfully", vetId);
    }
}
