package com.softeng.backend.models.user;

public interface IUser {

    String getEmail();
    String getFirstName();
    String getLastName();

    /**
     * Logic to detect if this user is invalid
     * @return true if any fields are null
     */
    boolean checkInvalidUser();

    /**
     * Logic to detect if this user is a empty user (no information at all)
     * @return true if identifying fields are empty/null
     */
    boolean checkEmptyUser();
}
