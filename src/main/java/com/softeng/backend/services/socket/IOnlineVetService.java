package com.softeng.backend.services.socket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IOnlineVetService {
    void addVetIds(@NotNull @NotBlank String sessionId, @NotNull @NotBlank String vetId);

    void removeSession(@NotNull @NotBlank String sessionId);

    boolean isOnline(@NotNull @NotBlank String vetId);

    List<String> getOnlineVetIds();
}
