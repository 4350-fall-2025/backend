package com.softeng.backend.models.user;

public interface IUser {

    public String getId();
    public String getEmail();
    public String getFirstName();
    public String getLastName();

    /**
     * Logic to detect if this user is a default/null user (no email/id)
     * eg. user = new User() or new Owner()
     * @return true if identifying fields are empty/null
     */
    public boolean checkInvalidUser();

    /**
     * Logic to detect if this user is a empty user (no information at all)
     * @return true if identifying fields are empty/null
     */
    public boolean checkEmptyUser();
}
