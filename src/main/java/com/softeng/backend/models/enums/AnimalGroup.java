package com.softeng.backend.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AnimalGroup {
    SMALL_MAMMAL,
    FARM,
    EQUINE,
    BIRD,
    REPTILE,
    AMPHIBIAN,
    FISH,
    INVERTEBRATE,
    OTHER;

    @JsonCreator
    public static AnimalGroup fromString(String value)
    {
        for (AnimalGroup animalGroup : AnimalGroup.values()) {
            if (animalGroup.name().equalsIgnoreCase(value)) {
                return animalGroup;
            }
        }
        return null;
    }
}
