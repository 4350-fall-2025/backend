package com.softeng.backend.models.user;


import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.models.user.vet.Vet;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    void testGetEmail() {
        User emptyOwner = new Owner("", "", "", "");
        User defaultOwner = new Owner();
        User emptyVet = new Vet("", "", "", "", "");
        User defaultVet = new Vet();
        User validOwner = new Owner("No", "Name", "email@gmail.com", "123");
        User vetUser = new Vet("Very", "Nice", "nice@outlook.com", "isSecure", "certified");

        assert(emptyOwner.getEmail().isEmpty());
        assert(defaultOwner.getEmail().isEmpty());
        assert(emptyVet.getEmail().isEmpty());
        assert(defaultVet.getEmail().isEmpty());
        assert(validOwner.getEmail().equals("email@gmail.com"));
        assert(vetUser.getEmail().equals("nice@outlook.com"));
    }

    @Test
    void testGetFirstName() {
        User emptyOwner = new Owner("", "", "", "");
        User defaultOwner = new Owner();
        User emptyVet = new Vet("", "", "", "", "");
        User defaultVet = new Vet();
        User validOwner = new Owner("No", "Name", "email@gmail.com", "123");
        User vetUser = new Vet("Very", "Nice", "nice@outlook.com", "isSecure", "certified");

        assert(emptyOwner.getFirstName().isEmpty());
        assert(defaultOwner.getFirstName().isEmpty());
        assert(emptyVet.getFirstName().isEmpty());
        assert(defaultVet.getFirstName().isEmpty());
        assert(validOwner.getFirstName().equals("No"));
        assert(vetUser.getFirstName().equals("Very"));
    }

    @Test
    void testGetLastName() {
        User emptyOwner = new Owner("", "", "", "");
        User defaultOwner = new Owner();
        User emptyVet = new Vet("", "", "", "",  "");
        User defaultVet = new Vet();
        User validOwner = new Owner("No", "Name", "email@gmail.com", "123");
        User vetUser = new Vet("Very", "Nice", "nice@outlook.com", "isSecure",  "certified");

        assert(emptyOwner.getLastName().isEmpty());
        assert(defaultOwner.getLastName().isEmpty());
        assert(emptyVet.getLastName().isEmpty());
        assert(defaultVet.getLastName().isEmpty());
        assert(validOwner.getLastName().equals("Name"));
        assert(vetUser.getLastName().equals("Nice"));

    }

    @Test
    void testCheckInvalidUser() {
        User trulyEmptyUser = new Owner("", "", "", "");
        User justInvalidOwner = new Owner("Name", "Name", "", "");
        User whiteSpaceTricksyUser = new Owner("\t", "\r    ", "     ", "");
        User tricksyVetUser = new Vet("\t", "\r", "      ", "", "");
        User validOwner = new Owner("Name", "Name", "email@gmail.com", "123");
        User validVet = new Vet("Very", "Nice", "nice@outlook.com", "isSecure", "certified");
        
        assert(trulyEmptyUser.checkInvalidUser());
        assert(justInvalidOwner.checkInvalidUser());
        assert(whiteSpaceTricksyUser.checkInvalidUser());
        assert(tricksyVetUser.checkInvalidUser());
        assert(!validOwner.checkInvalidUser());
        assert(!validVet.checkInvalidUser());
    }

    @Test
    void testCheckEmptyUser() {
        User trulyEmptyUser = new Owner("", "", "", "");
        User tricksyEmptyUser = new Owner("  ", "\t", "   ", "\n");
        User justInvalidOwner = new Owner("Name", "Name", "", "");
        User tricksyVetUser = new Vet("Very", "Tricksy", "      ", "", "certified");
        User validOwner = new Owner("Name", "Name", "email@gmail.com", "123");
        User validVet = new Vet("Very", "Nice", "nice@outlook.com", "isSecure",  "certified");

        assert(trulyEmptyUser.checkEmptyUser());
        assert(tricksyEmptyUser.checkEmptyUser());
        assert(!justInvalidOwner.checkEmptyUser());
        assert(!tricksyVetUser.checkEmptyUser());
        assert(!validOwner.checkEmptyUser());
        assert(!validVet.checkEmptyUser());
    }
}
