package com.softeng.backend.repository.pet;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PetRepository implements IPetRepository {

    @Autowired
    private Firestore firestore;
    @Autowired
    private OwnerRepository ownerRepository;

    private final String PET_COLLECTION = "pets";

    public PetRepository(Firestore firestore, OwnerRepository ownerRepository) {
        this.firestore = firestore;
        this.ownerRepository  = ownerRepository;
    }

    // =========================
    // CREATE
    // =========================
    // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    @Override
    public Pet createPet(String ownerId, Pet pet) throws ExecutionException, InterruptedException {
        OwnerDTO owner = ownerRepository.getOwnerById(ownerId);
        if(owner != null) {

            // The following code was copied/developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
            ApiFuture<DocumentReference> addedDocRef = firestore.collection(PET_COLLECTION).add(pet);
            DocumentReference reference = addedDocRef.get();
//            DocumentSnapshot snapshot= reference.get().get();

//            if (snapshot.exists()) {
//                pet = snapshot.toObject(Pet.class);
//                pet.setOwnerId(ownerId);
//                owner.getOwner().createPet(pet);
//            }
        }
        return pet;
    }

    // =========================
    // READ
    // =========================
    @Override
    public Pet getPetById(String petId) throws ExecutionException, InterruptedException {
        Pet result = new Pet();

        ApiFuture<DocumentSnapshot> future = firestore.collection(PET_COLLECTION).document(petId).get();
        DocumentSnapshot doc = future.get();

        if (doc.exists()) {
            result = doc.toObject(Pet.class);
        }

        return result;
    }

    @Override
    public List<PetLite> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException {
        List<PetLite> pets = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION).whereEqualTo("owner", ownerId).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot doc : documents) {
            pets.add(doc.toObject(PetLite.class));
        }

        return pets;
    }


    // =========================
    // UPDATE
    // Generated with GTP-4.1
    // =========================
    @Override
    public Pet updatePet(String petId, Pet pet) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = firestore.collection(PET_COLLECTION).document(petId).set(pet);
        writeResult.get(); // Wait for completion, throws if failed

        return pet;
    }

    // =========================
    // DELETE OPERATION
    // Generated with GTP-4.1
    // =========================
    @Override
    public Pet deletePet(String petId) throws ExecutionException, InterruptedException {
        Pet deletedPet = new Pet();
        ApiFuture<DocumentSnapshot> getFuture = firestore.collection(PET_COLLECTION).document(petId).get();
        DocumentSnapshot doc = getFuture.get();
        if (doc.exists()) {
            deletedPet = doc.toObject(Pet.class);
            ApiFuture<WriteResult> deleteFuture = firestore.collection(PET_COLLECTION).document(petId).delete();
            deleteFuture.get(); // Wait for completion
        }
        return deletedPet;
    }
}
