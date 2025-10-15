package com.softeng.backend.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PetSexType {
    MALE,
    FEMALE,
    UNKNOWN;

    @JsonCreator
    public static PetSexType fromString(String value) {
        for (PetSexType type : PetSexType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
