package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.socket.ChatMessage;
import com.softeng.backend.services.socket.RequestVetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatController {

    private static final String MESSAGE_DESTINATION = "/queue/message";
    private final SimpMessagingTemplate template;
    private final RequestVetService requestVetService;

    @Autowired
    public ChatController(RequestVetService requestVetService, SimpMessagingTemplate template) {
        this.template = template;
        this.requestVetService = requestVetService;
    }

    @MessageMapping("/message")
    public void sendMessage(ChatMessage message) {
        // deliver to recipient's personal queue
        if (requestVetService.isAccepted(message.getFrom(),  message.getTo())) {
            template.convertAndSendToUser(message.getTo(), MESSAGE_DESTINATION, message);
            log.info("Private message sent to {}", message.getTo());
        }
        else {
            ChatMessage rejectMessage = new ChatMessage("server", message.getFrom(), "Cannot send message, request not accepted.");
            template.convertAndSendToUser(message.getFrom(), MESSAGE_DESTINATION, rejectMessage);
        }
    }
}
