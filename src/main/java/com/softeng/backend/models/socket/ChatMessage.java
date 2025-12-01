package com.softeng.backend.models.socket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
public class ChatMessage {
    @NotNull
    @NotBlank
    private String from;
    @NotNull
    @NotBlank
    private String to;
    @NotNull
    @NotBlank
    private String message;
}
