package com.softeng.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String username = null;
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest http = servletRequest.getServletRequest();
            username = http.getParameter("username");
        }
        if (username == null || username.isBlank()) {
            username = "anon-" + UUID.randomUUID();
        }
        return new StompPrincipal(username);
    }
}