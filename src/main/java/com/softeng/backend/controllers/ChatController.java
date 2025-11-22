package com.softeng.backend.controllers;

import com.softeng.backend.models.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;

    @Autowired
    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/private")
    public void sendPrivate(ChatMessage message) {
        if (message == null || message.getSender() == null || message.getRecipient() == null) return;
        message.setTimestamp(System.currentTimeMillis());
        // deliver to recipient's personal queue
        template.convertAndSendToUser(message.getRecipient(), "/queue/messages", message);
        // echo back to sender so sender's UI can also render the sent message
        template.convertAndSendToUser(message.getSender(), "/queue/messages", message);
    }
}
