package com.softeng.backend.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
public class StompSubscribeListener {

    private static final Logger logger = LoggerFactory.getLogger(StompSubscribeListener.class);

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        String subscriptionId = sha.getSubscriptionId();
        String destination = sha.getDestination();
        Principal user = sha.getUser();
        String username = user != null ? user.getName() : "anonymous";

        logger.info("STOMP SUBSCRIBE session={} user={} subscriptionId={} destination={}",
                sessionId, username, subscriptionId, destination);
    }
}
