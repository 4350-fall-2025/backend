package com.softeng.backend.repository.pet;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
 * The following class was partially developed with
 * IntelliJ autocomplete and Copilot (GPT-4.1)
 */

@Slf4j
@Repository
public class PetRepository implements IPetRepository {

    private final Firestore firestore;
    private final OwnerRepository ownerRepository;

    private final String PET_COLLECTION = "pets";

    @Autowired
    public PetRepository(Firestore firestore, OwnerRepository ownerRepository) {
        this.firestore = firestore;
        this.ownerRepository = ownerRepository;
    }

    // =========================
    // CREATE
    // =========================
    // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document

    /**
     *
     * @param ownerId Document ID of the pet owner
     * @param pet <code>Pet</code> object to insert into database
     * @return A DTO of the created pet. The content may be empty if <code>pet</code>
     * is invalid
     * @throws ExecutionException if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    @Override
    public PetDTO createPet(String ownerId, Pet pet) throws ExecutionException, InterruptedException {
        OwnerDTO owner = ownerRepository.getOwnerById(ownerId);
        PetDTO result = new PetDTO();

        if (owner != null && pet.isValid()) {
            pet.setOwnerId(ownerId);
            // The following code was copied/developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
            ApiFuture<DocumentReference> addedDocRef = firestore.collection(PET_COLLECTION)
                                                                .add(pet);
            DocumentReference reference = addedDocRef.get();
            String generatedId = reference.getId();
            pet.setId(generatedId);
            ApiFuture<WriteResult> writeResult = reference.set(pet);
            writeResult.get();

            DocumentSnapshot snapshot = reference.get().get();
            if (snapshot.exists()) {
                pet = snapshot.toObject(Pet.class);
                if (pet != null && pet.isValid()) {
                    ownerRepository.addPet(ownerId, new PetLite(generatedId, pet.getName(), pet.getBreed()));
                    result = new PetDTO(pet.getId(), pet);
                }
            }
        }
        return result;
    }

    // =========================
    // READ
    // =========================

    /**
     * @param ownerId Document ID of the pet owner
     * @param petId Document ID of the pet to retrieve
     * @return A DTO of the pet. The content may be empty if no pet with
     * <code>petId</code> exists
     * @throws ExecutionException if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    @Override
    public List<PetDTO> getPetById(String ownerId, String petId) throws ExecutionException, InterruptedException {
        List<PetDTO> result = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION)
                .whereEqualTo("ownerId", ownerId).whereEqualTo("id", petId).get();

        QueryDocumentSnapshot doc = future.get().getDocuments().getFirst();

        if (doc.exists()) {
            Pet pet = doc.toObject(Pet.class);
            if (pet.isValid()) {
                result.add(new PetDTO(pet.getId(), pet));
            }
        }

        return result;
    }

    /**
     *
     * @param ownerId Document ID of the pet owner
     * @return A list of lightweight pet objects owned by the owner with
     * <code>ownerId</code>.
     * @throws ExecutionException   if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    @Override
    public List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException {
        List<PetDTO> result = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION)
                .whereEqualTo("ownerId", ownerId).get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        for (QueryDocumentSnapshot doc: docs) {
            Pet pet = doc.toObject(Pet.class);
            result.add(new PetDTO(pet.getId(), pet));
        }

        return result;
    }


    // =========================
    // UPDATE
    // =========================

    /**
     * @param ownerId Document ID of the pet owner
     * @param petId Document ID of the pet to update
     * @param pet   <code>Pet</code> object containing updated information
     * @return The updated <code>Pet</code> object
     * @throws ExecutionException   if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    @Override
    public PetDTO updatePet(String ownerId, String petId, Pet pet) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();

        if (pet != null && pet.isValid()) {
            ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION)
                    .whereEqualTo("ownerId", ownerId)
                    .whereEqualTo("id", petId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentReference docRef = documents.getFirst().getReference();
                // Set the new pet data
                pet.setId(petId);
                pet.setOwnerId(ownerId);
                ApiFuture<WriteResult> writeResult = docRef.set(pet);
                writeResult.get();

                result = new PetDTO(pet.getId(), pet);
            }
        }

        return result;
    }

    // =========================
    // DELETE OPERATION
    // =========================

    /**
     * @param petId Document ID of the pet to delete
     * @return The deleted <code>Pet</code> object. If no pet with
     * <code>petId</code> exists, an empty <code>Pet</code> object is returned
     * @throws ExecutionException   if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    @Override
    public PetDTO deletePet(String ownerId, String petId) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();

        ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .whereEqualTo("id", petId)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            DocumentReference docRef = documents.getFirst().getReference();
            DocumentSnapshot doc = docRef.get().get();
            if (doc.exists()) {
                Pet deletedPet = doc.toObject(Pet.class);
                if (deletedPet != null && deletedPet.isValid()) {
                    result = new PetDTO(deletedPet.getId(), deletedPet);
                    docRef.delete().get();
                }
            }
        }
        return result;
    }
}
