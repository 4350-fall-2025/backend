package com.softeng.backend.models;

public class RegisterMessage {
    private String username;

    public RegisterMessage() {}

    public RegisterMessage(String username) { this.username = username; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
