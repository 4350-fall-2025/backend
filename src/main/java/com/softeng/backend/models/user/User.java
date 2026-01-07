package com.softeng.backend.models.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class User {

    protected String email = "";
    protected String firstName = "";
    protected String lastName = "";
    protected String password = ""; // TODO: remove this once we set up actual secure auth

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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
        return ( email == null || email.isBlank() ) ||
                ( lastName == null || lastName.isBlank() )  ||
                ( firstName == null || firstName.isBlank() ) ||
                ( password == null || password.isBlank() );
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
