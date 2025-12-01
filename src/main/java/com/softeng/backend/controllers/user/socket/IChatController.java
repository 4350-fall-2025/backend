package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.socket.ChatMessage;

public interface IChatController {
    void sendMessage(ChatMessage chatMessage);
}
