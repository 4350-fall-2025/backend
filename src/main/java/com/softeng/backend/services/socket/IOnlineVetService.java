package com.softeng.backend.services.socket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IOnlineVetService {
    void addUserIds(@NotNull @NotBlank String sessionId, @NotNull @NotBlank String userId);

    void removeSession(@NotNull @NotBlank String sessionId);

    boolean isOnline(@NotNull @NotBlank String userId);
    List<String> getOnlineUserIds();
}
