package com.softeng.backend.models.pet;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SterileStatus {
    STERILE,
    NON_STERILE,
    UNKNOWN;


    /**
     * Return the SterileStatus from string.
     * Generated with GPT-4.1
     * @param value String value, should be one of "STERILE", "NON_STERILE",
     *              or "UNKNOWN"
     * @returns the corresponding SterileStatus enum value, or UNKNOWN by default
     */
    @JsonCreator
    public static SterileStatus fromString(String value) {
        for (SterileStatus status : SterileStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return UNKNOWN; // Default value if no match is found
    }
}
