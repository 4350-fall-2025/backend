package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.enums.RequestStatus;
import com.softeng.backend.models.socket.RequestMessage;
import com.softeng.backend.services.socket.OnlineVetService;
import com.softeng.backend.services.socket.RequestVetService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class RequestVetController {

    private static final String REQUEST_DESTINATION = "/queue/requests";
    private final RequestVetService requestVetService;
    private final OnlineVetService onlineVetService;
    private final SimpMessagingTemplate template;

    @Autowired
    public RequestVetController(RequestVetService requestVetService, OnlineVetService onlineVetService, SimpMessagingTemplate template) {
        this.requestVetService = requestVetService;
        this.onlineVetService = onlineVetService;
        this.template = template;
    }

    @MessageMapping("/vet/request")
    public void requestVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetVetId = requestMessage.getTo();
        String sourceOwnerId = requestMessage.getFrom();
        String petId = requestMessage.getPetId();

        if (onlineVetService.isOnline(requestMessage.getTo())) {
            requestVetService.requestVet(sourceOwnerId, targetVetId);
            RequestMessage response = new RequestMessage(sourceOwnerId, targetVetId, petId, RequestStatus.PENDING);
            // send incoming request to the target vet personal queue
            template.convertAndSendToUser(targetVetId, REQUEST_DESTINATION, response);
            log.info("Connection request sent from {} to {}", sourceOwnerId, targetVetId);
        } else {
            // inform requester that target is offline
            RequestMessage error = new RequestMessage(sourceOwnerId, targetVetId, petId, RequestStatus.REJECTED);
            template.convertAndSendToUser(sourceOwnerId, REQUEST_DESTINATION, error);
            log.info("Connection request sent from {} failed because {} is offline", sourceOwnerId, targetVetId);
        }
    }

    @MessageMapping("/vet/accept")
    public void acceptRequest(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetOwnerId = requestMessage.getTo();
        String sourceVetId = requestMessage.getFrom();
        String petId = requestMessage.getPetId();
        requestVetService.acceptRequest(targetOwnerId, sourceVetId);
        // notify both parties that the request was accepted
        RequestMessage acceptMessage = new RequestMessage(sourceVetId, targetOwnerId, petId, RequestStatus.ACCEPTED);
        template.convertAndSendToUser(targetOwnerId, REQUEST_DESTINATION, acceptMessage);
        template.convertAndSendToUser(sourceVetId, REQUEST_DESTINATION, acceptMessage);
        log.info("Connection request from {} to {} accepted", targetOwnerId, sourceVetId);
    }

    @MessageMapping("/vet/reject")
    public void cancelRequestFromVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetOwnerId = requestMessage.getTo();
        String sourceVetId = requestMessage.getFrom();
        String petId = requestMessage.getPetId();
        requestVetService.removeRequest(targetOwnerId, sourceVetId);
        // notify the owner that the vet has canceled the request
        RequestMessage rejectMessage = new RequestMessage(sourceVetId, targetOwnerId, petId, RequestStatus.REJECTED);
        template.convertAndSendToUser(targetOwnerId, REQUEST_DESTINATION, rejectMessage);
        log.info("Connection request from {} to {} rejected", targetOwnerId, sourceVetId);
    }

    @MessageMapping("/owner/cancel")
    public void cancelRequestFromOwner(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor) {
        String targetVetId = requestMessage.getTo();
        String sourceOwnerId = requestMessage.getFrom();
        String petId = requestMessage.getPetId();
        requestVetService.removeRequest(sourceOwnerId, targetVetId);
        // notify the vet that the owner has canceled the request
        RequestMessage cancelMessage = new RequestMessage(sourceOwnerId, targetVetId, petId, RequestStatus.CANCELED);
        template.convertAndSendToUser(targetVetId, REQUEST_DESTINATION, cancelMessage);
        log.info("Connection request from {} to {} cancelled", sourceOwnerId, targetVetId);
    }
}
