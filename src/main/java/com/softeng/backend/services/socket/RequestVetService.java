package com.softeng.backend.services.socket;

import com.softeng.backend.models.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RequestVetService {
    private final ConcurrentHashMap<String, RequestStatus> userIdsToRequestStatus = new ConcurrentHashMap<>();

    private String key(String userIdA, String userIdB) {
        return (userIdA.compareTo(userIdB) <= 0) ? (userIdA + ":" + userIdB) : (userIdB + ":" + userIdA);
    }

    public void requestVet(@NotNull @NotBlank String ownerId, @NotNull @NotBlank String vetId) {
        userIdsToRequestStatus.put(key(ownerId, vetId), RequestStatus.PENDING);
    }

    public void acceptRequest(@NotNull @NotBlank String ownerId, @NotNull @NotBlank String vetId) {
        String k = key(ownerId, vetId);
        if (userIdsToRequestStatus.containsKey(k)) {
            userIdsToRequestStatus.put(k, RequestStatus.ACCEPTED);
        }
    }

    public void removeRequest(@NotNull @NotBlank String userIdA, @NotNull @NotBlank String userIdB) {
        userIdsToRequestStatus.remove(key(userIdA, userIdB));
    }

    public boolean isAccepted(@NotNull @NotBlank String userIdA, @NotNull @NotBlank String userIdB) {
        String k = key(userIdA, userIdB);
        if (userIdsToRequestStatus.containsKey(k)) {
            RequestStatus status = userIdsToRequestStatus.get(k);
            return status == RequestStatus.ACCEPTED;
        }
        return false;
    }

    public List<String> removeAllRequestsByUserId(@NotNull @NotBlank String userId) {
        List<String> counterparts = new ArrayList<>();
        Iterator<String> it = userIdsToRequestStatus.keySet().iterator();
        while (it.hasNext()) {
            String k = it.next();
            String[] parts = k.split(":");
            String ownerId = parts[0];
            String vetId = parts[1];
            if (ownerId.equals(userId)) {
                counterparts.add(vetId);
                it.remove();
            } else if (vetId.equals(userId)) {
                counterparts.add(ownerId);
                it.remove();
            }
        }
        return counterparts;
    }
}
