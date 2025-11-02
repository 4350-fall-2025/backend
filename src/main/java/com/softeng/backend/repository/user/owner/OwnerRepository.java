package com.softeng.backend.repository.user.owner;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.owner.Owner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Owners Endpoint
 * General References:
 * <a href="https://masteringbackend.com/posts/spring-boot">...</a>
 * <a href="https://firebase.google.com/docs/firestore/manage-data/add-data">...</a>
 * <p>
 *
 * The following code was developed with guidance from OpenAI's ChatGPT (<a href="https://chat.openai.com">...</a>)
 * and IntelliJ autocomplete.
 * I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot or firestore
 * class/method worked.
 * Autocomplete was used to create boilerplate and/or duplicate code across methods.
 */

@Repository
public class OwnerRepository implements IOwnerRepository {

    @Autowired
    private Firestore firestore;
    private final String collectionName = "owners";
    private static final Logger logger = LoggerFactory.getLogger(OwnerRepository.class);

    public OwnerRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    public OwnerDTO createOwner(Owner owner) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = firestore.collection(collectionName).add(owner);
        return new OwnerDTO(addedDocRef.get().getId(), owner);
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/query-data/get-data
    public OwnerDTO getOwnerByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            Owner owner = documents.getFirst().toObject(Owner.class);
            return new OwnerDTO(documents.getFirst().getId(), owner);
        } else {
            logger.info("DEBUG LOG: No documents found for email {}", email);
            return new OwnerDTO();
        }
    }

    // https://firebase.google.com/docs/firestore/query-data/get-data
    public OwnerDTO getOwnerById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = firestore.collection(collectionName).document(id).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Owner owner = document.toObject(Owner.class);
            return new OwnerDTO(document.getId(), owner);
        }

        return new OwnerDTO();
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
    // This function has code copied from ChatGPT, which are commented in line
    public OwnerDTO updateOwner(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collectionName).document(id);
        DocumentSnapshot snapshot = docRef.get().get();

        // Asked ChatGPT how to handle specific exception that occurs when you call update on invalid doc id
        if (!snapshot.exists()) {
            return new OwnerDTO();
        }

        docRef.update(updateFields).get();
        snapshot = docRef.get().get();
        // Asked ChatGPT how we can get the updated result after writing
        Owner owner = snapshot.toObject(Owner.class);
        return new OwnerDTO(snapshot.getId(), owner);
    }

    @Override
    public OwnerDTO addPet(String ownerId, PetLite pet) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collectionName).document(ownerId);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            logger.info("DEBUG LOG: Owner not found for id {}", ownerId);
            return new OwnerDTO();
        }

        // Get current pets array or create new
        List<Map<String, Object>> pets = (List<Map<String, Object>>) snapshot.get("pets");
        if (pets == null) {
            pets = new java.util.ArrayList<>();
        }

        // Convert PetLite to Map
        Map<String, Object> petMap = new java.util.HashMap<>();
        petMap.put("id", pet.getId());
        petMap.put("name", pet.getName());
        petMap.put("breed", pet.getBreed());
        pets.add(petMap);

        // Update pets array in Firestore
        docRef.update("pets", pets).get();

        // Return updated OwnerDTO
        DocumentSnapshot updatedSnapshot = docRef.get().get();
        Owner updatedOwner = updatedSnapshot.toObject(Owner.class);
        return new OwnerDTO(updatedSnapshot.getId(), updatedOwner);
    }

    public OwnerDTO updatePet(String ownerId, PetLite subdocument) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collectionName).document(ownerId);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            logger.info("DEBUG LOG: Owner not found for id {}", ownerId);
            return new OwnerDTO();
        }

        // Get current pets array
        List<Map<String, Object>> pets = (List<Map<String, Object>>) snapshot.get("pets");
        if (pets == null) {
            logger.info("DEBUG LOG: No pets found for owner id {}", ownerId);
            return new OwnerDTO(snapshot.getId(), snapshot.toObject(Owner.class));
        }

        // Find and update the pet map
        boolean updated = false;
        for (int i = 0; i < pets.size() && !updated; i++) {
            Map<String, Object> petMap = pets.get(i);
            if (petMap != null && subdocument.getId() != null && subdocument.getId().equals(petMap.get("id"))) {
                Map<String, Object> newPetMap = new java.util.HashMap<>();
                newPetMap.put("id", subdocument.getId());
                newPetMap.put("name", subdocument.getName());
                newPetMap.put("breed", subdocument.getBreed());
                pets.set(i, newPetMap);
                updated = true;
            }
        }

        if (!updated) {
            logger.info("DEBUG LOG: Pet with id {} not found for owner id {}", subdocument.getId(), ownerId);
            return new OwnerDTO(snapshot.getId(), snapshot.toObject(Owner.class));
        }

        // Update pets array in Firestore
        docRef.update("pets", pets).get();

        // Return updated OwnerDTO
        DocumentSnapshot updatedSnapshot = docRef.get().get();
        Owner updatedOwner = updatedSnapshot.toObject(Owner.class);
        return new OwnerDTO(updatedSnapshot.getId(), updatedOwner);
    }

    public OwnerDTO removePet(String ownerId, String petId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collectionName).document(ownerId);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            logger.info("DEBUG LOG: Owner not found for id {}", ownerId);
            return new OwnerDTO();
        }

        // Get current pets array
        List<Map<String, Object>> pets = (List<Map<String, Object>>) snapshot.get("pets");
        if (pets == null) {
            logger.info("DEBUG LOG: No pets found for owner id {}", ownerId);
            return new OwnerDTO(snapshot.getId(), snapshot.toObject(Owner.class));
        }

        // Find and remove the pet map
        boolean removed = pets.removeIf(petMap -> petMap != null && petId.equals(petMap.get("id")));

        if (!removed) {
            logger.info("DEBUG LOG: Pet with id {} not found for owner id {}", petId, ownerId);
            return new OwnerDTO(snapshot.getId(), snapshot.toObject(Owner.class));
        }

        // Update pets array in Firestore
        docRef.update("pets", pets).get();

        // Return updated OwnerDTO
        DocumentSnapshot updatedSnapshot = docRef.get().get();
        Owner updatedOwner = updatedSnapshot.toObject(Owner.class);
        return new OwnerDTO(updatedSnapshot.getId(), updatedOwner);
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/manage-data/delete-data
    public boolean deleteOwner(String id) {
        try {
            ApiFuture<WriteResult> future = firestore.collection(collectionName).document(id).delete();
            WriteResult result = future.get(); // waits for completion
            logger.info("DEBUG LOG: Delete successfully at{}", result.getUpdateTime());
            return true;
        } catch (Exception e) {
            logger.error("ERROR LOG: Owner deleteOwner failed for id {} with error: {}", id, e.getMessage());
            return false;
        }
    }
}
