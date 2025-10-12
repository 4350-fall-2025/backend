package com.softeng.backend.repository.user.owner;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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
 * https://masteringbackend.com/posts/spring-boot
 * https://firebase.google.com/docs/firestore/manage-data/add-data
 * https://firebase.google.com/docs/firestore/query-data/get-data
 *
 * I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot or firestore
 * class/method worked.
 * When I used/copied code from ChatGPT, I added in-line comment to reference it.
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
    public String createOwner(Owner owner) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = firestore.collection(collectionName).add(owner);
        owner.setId(addedDocRef.get().getId());
        return addedDocRef.get().getId();
    }


    /*****************************************************************************
     * READ
     ******************************************************************************/

    public Owner getOwnerByEmail(String email) throws ExecutionException, InterruptedException {

        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            return documents.getFirst().toObject(Owner.class);
        } else {
            logger.info("DEBUG LOG: No documents found for email " + email);
            return new Owner();
        }
    }

    public Owner getOwnerById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = firestore.collection(collectionName).document(id).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Owner.class);
        }

        return new Owner();
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
    public Owner updateOwner(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException {

        DocumentReference docRef = firestore.collection(collectionName).document(id);
        firestore.collection(collectionName).document(id).set(updateFields, SetOptions.merge());

        // Return updated information
        DocumentSnapshot snapshot = docRef.get().get();

        return snapshot.toObject(Owner.class);
    }

}
