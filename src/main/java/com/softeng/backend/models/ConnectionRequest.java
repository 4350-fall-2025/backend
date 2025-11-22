package com.softeng.backend.models;

public class ConnectionRequest {
    private String from;
    private String to;
    private Boolean accepted;
    private String message;

    public ConnectionRequest() {}

    public ConnectionRequest(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public Boolean getAccepted() { return accepted; }
    public void setAccepted(Boolean accepted) { this.accepted = accepted; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
