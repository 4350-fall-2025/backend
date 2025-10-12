package com.softeng.backend.models.user.owner;

import com.softeng.backend.models.pet.Pet;

import java.util.ArrayList;

public interface IOwner {

    /**
     * @param pet to add
     * @return true if successful
     */
    boolean createPet(Pet pet);

    /**
     * @param pet to remove
     * @return true if successful
     */
    boolean removePet(Pet pet);

    /**
     * @param pet to update
     * @return true if successful
     */
    boolean updatePet(Pet pet);

    /**
     * @return the owner's pets, otherwise empty list
     */
    ArrayList<Pet> getPets();
}
