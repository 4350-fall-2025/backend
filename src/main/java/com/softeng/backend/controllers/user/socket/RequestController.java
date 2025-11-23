package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.message.ConnectionRequest;
import com.softeng.backend.services.user.OnlineUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
    private final SimpMessagingTemplate template;
    private final OnlineUserService onlineUserService;

    @Autowired
    public RequestController(SimpMessagingTemplate template, OnlineUserService onlineUserService) {
        this.template = template;
        this.onlineUserService = onlineUserService;
    }

    @MessageMapping("/request")
    public void sendRequest(ConnectionRequest req) {
        if (req == null || req.getFrom() == null || req.getTo() == null) return;

        if (onlineUserService.isOnline(req.getTo())) {
            // send incoming request to the target user's personal queue
            template.convertAndSendToUser(req.getTo(), "/queue/requests", req);
            logger.info("Connection request sent from {} to {}", req.getFrom(), req.getTo());
        } else {
            // inform requester that target is offline
            ConnectionRequest error = new ConnectionRequest(req.getFrom(), req.getTo());
            error.setAccepted(false);
            error.setMessage("User is offline");
            template.convertAndSendToUser(req.getFrom(), "/queue/requests", error);
            logger.info("Connection request sent from {} failed because {} is offline", req.getFrom(), req.getTo());
        }
    }
}