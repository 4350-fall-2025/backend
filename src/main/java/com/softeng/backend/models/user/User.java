package com.softeng.backend.models.user;

import com.softeng.backend.controllers.user.owner.OwnerController;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public abstract class User implements IUser {

    protected String id;
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String password;
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    public User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public boolean setId(String id) {
        if(id != null && !id.isBlank()) {
            this.id = id;
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean checkInvalidUser() {
        return email == null || email.isBlank();
    }

    public boolean checkEmptyUser() {
        return ( email == null || email.isBlank() ) &&
                ( lastName == null || lastName.isBlank() )  &&
                ( firstName == null || firstName.isBlank() ) &&
                ( password == null || password.isBlank() );
    }

    public String getPassword() {
        return password;
    }

}
