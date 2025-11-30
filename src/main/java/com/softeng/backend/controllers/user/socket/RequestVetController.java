package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.socket.RequestMessage;
import com.softeng.backend.services.socket.OnlineVetService;
import com.softeng.backend.services.socket.RequestVetService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RequestVetController implements IRequestVetController {
    private static final Logger logger = LoggerFactory.getLogger(RequestVetController.class);
    private final RequestVetService requestVetService;
    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public RequestVetController(RequestVetService requestVetService, OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.requestVetService = requestVetService;
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @Override
    @MessageMapping("/vet/request")
    public void requestVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetVetId = requestMessage.getTo();
        String sourceOwnerId = requestMessage.getFrom();

        if (onlineVetService.isOnline(requestMessage.getTo())) {
            requestVetService.requestVet(sourceOwnerId, targetVetId);
            // send incoming request to the target vet personal queue
            template.convertAndSendToUser(targetVetId, "/queue/requests", requestMessage);
            logger.info("Connection request sent from {} to {}", sourceOwnerId, targetVetId);
        } else {
            // inform requester that target is offline
            RequestMessage error = new RequestMessage(sourceOwnerId, targetVetId, false, "User is offline");
            template.convertAndSendToUser(sourceOwnerId, "/queue/requests", error);
            logger.info("Connection request sent from {} failed because {} is offline", sourceOwnerId, targetVetId);
        }
    }

    @Override
    @MessageMapping("/vet/accept")
    public void acceptRequest(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetOwnerId = requestMessage.getTo();
        String sourceVetId = requestMessage.getFrom();
        requestVetService.acceptRequest(targetOwnerId, sourceVetId);
        // notify both parties that the request was accepted
        template.convertAndSendToUser(targetOwnerId, "/queue/requests", requestMessage);
        template.convertAndSendToUser(sourceVetId, "/queue/requests", requestMessage);
    }

    @Override
    @MessageMapping("/vet/cancel")
    public void cancelRequestFromVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetOwnerId = requestMessage.getTo();
        String sourceVetId = requestMessage.getFrom();
        requestVetService.cancelRequest(targetOwnerId, sourceVetId);
        // notify the owner that the vet has canceled the request
        template.convertAndSendToUser(targetOwnerId, "/queue/requests", requestMessage);
    }

    @Override
    @MessageMapping("/owner/cancel")
    public void cancelRequestFromOwner(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetVetId = requestMessage.getTo();
        String sourceOwnerId = requestMessage.getFrom();
        requestVetService.cancelRequest(sourceOwnerId, targetVetId);
        // notify the vet that the owner has canceled the request
        template.convertAndSendToUser(targetVetId, "/queue/requests", requestMessage);
    }
}
