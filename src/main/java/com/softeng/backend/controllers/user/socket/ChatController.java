package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.socket.ChatMessage;
import com.softeng.backend.services.socket.RequestVetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController implements IChatController {

    private static final String MESSAGE_DESTINATION = "/queue/message";
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate template;
    private final RequestVetService requestVetService;

    @Autowired
    public ChatController(RequestVetService requestVetService, SimpMessagingTemplate template) {
        this.template = template;
        this.requestVetService = requestVetService;
    }

    @Override
    @MessageMapping("/message")
    public void sendMessage(ChatMessage message) {
        // deliver to recipient's personal queue
        if (requestVetService.isAccepted(message.getFrom(),  message.getTo())) {
            template.convertAndSendToUser(message.getTo(), MESSAGE_DESTINATION, message);
            logger.info("Private message sent to {}", message.getTo());
        }
        else {
            ChatMessage rejectMessage = new ChatMessage("server", message.getFrom(), "Cannot send message, request not accepted.");
            template.convertAndSendToUser(message.getFrom(), MESSAGE_DESTINATION, rejectMessage);
        }
    }
}
