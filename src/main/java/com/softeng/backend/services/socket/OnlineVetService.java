package com.softeng.backend.services.socket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OnlineVetService implements IOnlineVetService {
    private final ConcurrentHashMap<String, String> sessionIdToUserIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userIdToSessionId = new ConcurrentHashMap<>();

    public void addUserIds(@NotNull @NotBlank String sessionId, @NotNull @NotBlank String userId) {
        if (sessionId == null || userId == null) return;
        sessionIdToUserIds.put(sessionId, userId);
        userIdToSessionId.put(userId, sessionId);
    }

    public void removeSession(@NotNull @NotBlank String sessionId) {
        if (sessionId == null) return;
        String userId = sessionIdToUserIds.remove(sessionId);
        if (userId != null) {
            userIdToSessionId.remove(userId);
        }
    }

    public boolean isOnline(@NotNull @NotBlank String userId) {
        return userId != null && userIdToSessionId.containsKey(userId);
    }

    public List<String> getOnlineUserIds() {
        return userIdToSessionId.keySet().stream().sorted().collect(Collectors.toList());
    }
}
