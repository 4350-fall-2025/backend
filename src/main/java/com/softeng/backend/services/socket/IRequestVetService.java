package com.softeng.backend.services.socket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IRequestVetService {
    void requestVet(@NotNull @NotBlank String ownerId,@NotNull @NotBlank String vetId);
    void acceptRequest(@NotNull @NotBlank String ownerId,@NotNull @NotBlank String vetId);
    void cancelRequest(@NotNull @NotBlank String ownerId,@NotNull @NotBlank String vetId);
    List<String> removeAllRequestsByUserId(@NotNull @NotBlank String userId);
}
