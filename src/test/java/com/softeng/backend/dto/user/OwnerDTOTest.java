package com.softeng.backend.dto.user;

import com.softeng.backend.dto.OwnerDTO;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class OwnerDTOTest {
    @Test
    void TestToMap() {

        OwnerDTO ownerDTO = new OwnerDTO();
        Map<String, Object> map = ownerDTO.toMap();

        assert(map.containsKey("id"));
        assert(map.get("firstName").toString().isEmpty());
        assert(map.get("lastName").toString().isEmpty());
        assert(map.get("email").toString().isEmpty());
        // TODO: update after auth set up:
        assert(map.get("password").toString().equals("MockTokenForNow"));
        /*return Map.of(
                "id", id,
                "firstName", owner.getFirstName(),
                "lastName", owner.getLastName(),
                "email", owner.getEmail(),
                "token", "MockTokenForNow"
        );*/
    }
}
