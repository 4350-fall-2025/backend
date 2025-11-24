package com.softeng.backend.models.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OnlineVetMessage {
    @NotNull
    @NotBlank
    private String vetId;
}
