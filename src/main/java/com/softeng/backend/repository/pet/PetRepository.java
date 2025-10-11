package com.softeng.backend.repository.pet;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class PetRepository implements IPetRepository {

    @Autowired
    private Firestore firestore;
    @Autowired
    private OwnerRepository ownerRepository;

    public PetRepository(Firestore firestore, OwnerRepository ownerRepository) {
        this.firestore = firestore;
        this.ownerRepository  = ownerRepository;
    }

    // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    public Pet createPet(String ownerId, Pet pet) {
        Pet result = new Pet();
        try {
            Owner owner = ownerRepository.getOwnerById(ownerId);
            if(owner != null) {

                // reference: got the following code to read-after insert from ChatGPT (how to access object after inserting)
                ApiFuture<DocumentReference> addedDocRef = firestore.collection("pets").add(pet);
                DocumentReference reference = addedDocRef.get();
                DocumentSnapshot snapshot= reference.get().get();

                if (snapshot.exists()) {
                    pet = snapshot.toObject(Pet.class);
                    owner.createPet(pet);
                }
            }
        } catch(ExecutionException | InterruptedException e) {
            System.out.println("Error: could not create user. Please try again, then check debug logs." + e.getMessage());
        }
        return pet;
    }
}
