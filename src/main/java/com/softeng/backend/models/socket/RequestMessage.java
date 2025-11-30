package com.softeng.backend.models.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RequestMessage {
    private String from;
    private String to;
    private boolean accepted;
    private String message;
}
