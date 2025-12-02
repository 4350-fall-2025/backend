package com.softeng.backend.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//websocket configuration code from spring documentation
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // broker for public topics and private queues
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        // prefix used for sending to a specific user
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Raw websocket endpoint (clients should connect with ?username=Alice)
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new UserHandshakeHandler())
                .addInterceptors(new UserHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:3001");

        // SockJS endpoint (browsers)
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new UserHandshakeHandler())
                .addInterceptors(new UserHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:3001")
                .withSockJS();
    }
}
