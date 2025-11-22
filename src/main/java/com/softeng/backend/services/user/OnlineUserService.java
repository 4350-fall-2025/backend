package com.softeng.backend.services.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OnlineUserService {
    private final ConcurrentHashMap<String, String> sessionIdToUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userToSessionId = new ConcurrentHashMap<>();

    public void addUser(String sessionId, String username) {
        if (sessionId == null || username == null) return;
        sessionIdToUser.put(sessionId, username);
        userToSessionId.put(username, sessionId);
    }

    public void removeSession(String sessionId) {
        if (sessionId == null) return;
        String username = sessionIdToUser.remove(sessionId);
        if (username != null) {
            userToSessionId.remove(username);
        }
    }

    public boolean isOnline(String username) {
        return username != null && userToSessionId.containsKey(username);
    }

    public List<String> getOnlineUsers() {
        return userToSessionId.keySet().stream().sorted().collect(Collectors.toList());
    }
}
