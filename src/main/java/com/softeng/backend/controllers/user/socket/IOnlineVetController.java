package com.softeng.backend.controllers.user.socket;

import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IOnlineVetController {
    void Online(@NotNull SimpMessageHeaderAccessor headerAccessor);
}
