package com.softeng.backend.repository.user.vet;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;
import lombok.extern.slf4j.Slf4j;
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
 * I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot or firestore
 * class/method worked.
 */

@Slf4j
@Repository
public class VetRepository {

    private final Firestore firestore;
    private final String collectionName = "vets";

    @Autowired
    public VetRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    // reference: https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    public VetDTO createVet(Vet vet) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = firestore.collection(collectionName).add(vet);
        return new VetDTO(addedDocRef.get().getId(), vet);
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/query-data/get-data
    public VetDTO getVetByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            Vet vet = documents.getFirst().toObject(Vet.class);
            return new VetDTO(documents.getFirst().getId(), vet);
        } else {
            log.info("DEBUG LOG: No documents found for email {}", email);
            return new VetDTO();
        }
    }

    // https://firebase.google.com/docs/firestore/query-data/get-data
    public VetDTO getVetById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = firestore.collection(collectionName).document(id).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Vet vet = document.toObject(Vet.class);
            return new VetDTO(document.getId(), vet);
        }

        return new VetDTO();
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
    public VetDTO updateVet(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collectionName).document(id);
        DocumentSnapshot snapshot = docRef.get().get();

        // Asked ChatGPT how to handle specific exception that occurs when you call update on invalid doc id
        if (!snapshot.exists()) {
            return new VetDTO();
        }


        docRef.update(updateFields).get();
        // The following code was copied from OpenAI's ChatGPT (https://chat.openai.com))
        // I asked ChatGPT how we can get the updated result after writing,
        snapshot = docRef.get().get();
        Vet vet = snapshot.toObject(Vet.class);
        return new VetDTO(snapshot.getId(), vet);
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/manage-data/delete-data
    public void deleteVet(String id) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<WriteResult> future = firestore.collection(collectionName).document(id).delete();
            WriteResult result = future.get(); // waits for completion
            log.info("DEBUG LOG: Delete successfully at{}", result.getUpdateTime());
        } catch (Exception e) {
            log.error("ERROR LOG: vet deleteVet failed for id {} with error: {}", id, e.getMessage());
        }
    }
}
