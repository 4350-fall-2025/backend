package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.message.OnlineVetMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IOnlineVetController {
    void Online(@NotNull OnlineVetMessage msg, @NotNull SimpMessageHeaderAccessor headerAccessor);
}
