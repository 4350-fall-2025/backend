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
public class RequestVetService implements IRequestVetService {
    private final ConcurrentHashMap<String, RequestStatus> userIdsToRequestStatus = new ConcurrentHashMap<>();

    private String key(String ownerId, String vetId) {
        return ownerId + ":" + vetId;
    }

    @Override
    public void requestVet(@NotNull @NotBlank String ownerId, @NotNull @NotBlank String vetId) {
        userIdsToRequestStatus.put(key(ownerId, vetId), RequestStatus.PENDING);
    }

    @Override
    public void acceptRequest(@NotNull @NotBlank String ownerId, @NotNull @NotBlank String vetId) {
        String k = key(ownerId, vetId);
        if (userIdsToRequestStatus.containsKey(k)) {
            userIdsToRequestStatus.put(k, RequestStatus.ACCEPTED);
        }
    }

    @Override
    public void cancelRequest(@NotNull @NotBlank String ownerId, @NotNull @NotBlank String vetId) {
        userIdsToRequestStatus.remove(key(ownerId, vetId));
    }

    @Override
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
