package com.softeng.backend.models.user.owner;

import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.User;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Document(collectionName = "owners")
public class Owner extends User implements IOwner {

    private ArrayList<Pet> pets;

    public Owner(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        pets = new ArrayList<>();
    }

    public Owner(String firstName, String lastName, String email, String password, ArrayList<Pet> pets) {
        super(firstName, lastName, email, password);
        this.pets = pets;
    }

    /// ////////////////////////////////////////////////////
    ///TODO: implement pet CRUD
    /// ///////////////////////////////////////////////////

    @Override
    public boolean createPet(Pet pet) {
        return pets.add(pet);
    }

    @Override
    public boolean removePet(Pet pet) {
        return pets.remove(pet);
    }

    @Override
    public boolean updatePet(Pet pet) {
        return false;
    }

    @Override
    public ArrayList<Pet> getPets() {
        return null;
    }
}
