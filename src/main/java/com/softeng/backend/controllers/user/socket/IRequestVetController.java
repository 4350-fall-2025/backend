package com.softeng.backend.controllers.user.socket;

import com.softeng.backend.models.socket.RequestMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IRequestVetController {
    void requestVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor);
    void acceptRequest(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor);
    void cancelRequestFromVet(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor);
    void cancelRequestFromOwner(@NotNull @NotBlank RequestMessage requestMessage, @NotNull SimpMessageHeaderAccessor headerAccessor);
}
