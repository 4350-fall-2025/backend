package com.softeng.backend.repository.user.owner;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.OwnerDTO;
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
 * The following code was developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
 * I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot or firestore
 * class/method worked.
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
            logger.info("DEBUG LOG: No documents found for email " + email);
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
    public OwnerDTO updateOwner(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException {

        DocumentReference docRef = firestore.collection(collectionName).document(id);
        docRef.update(updateFields).get();

        // The following code was copied from OpenAI's ChatGPT (https://chat.openai.com))
        // I asked ChatGPT how we can get the updated result after writing,
        DocumentSnapshot snapshot = docRef.get().get();
        Owner owner = snapshot.toObject(Owner.class);
        return new OwnerDTO(snapshot.getId(), owner);
    }

}
