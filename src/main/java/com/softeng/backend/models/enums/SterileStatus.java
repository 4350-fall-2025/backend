package com.softeng.backend.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SterileStatus {
    STERILE,
    NON_STERILE,
    UNKNOWN;


    /**
     * Return the SterileStatus from string.
     * Generated with GPT-4.1
     */
    @JsonCreator
    public static SterileStatus fromString(String value) {
        for (SterileStatus status : SterileStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

}
