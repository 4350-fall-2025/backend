package com.softeng.backend.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.models.Vet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class VetRepository {

    private final Firestore firestore;

    public VetRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    // Referred to Firebase documentation:
    // https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
    public List<Vet> getAllVets() {

        CollectionReference vetsRef = firestore.collection("vets");
        ApiFuture<QuerySnapshot> future = vetsRef.get();
        List<Vet> vets = new ArrayList<>();

        try {
            QuerySnapshot collection = future.get();
            if (collection != null) {
                for (DocumentSnapshot doc : collection.getDocuments()) {
                    Vet vet = doc.toObject(Vet.class);
                    vets.add(vet);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO implement better error handling
            e.printStackTrace();
        }
        return vets;
    }

    // Referred to Firebase documentation:
    // https://firebase.google.com/docs/firestore/query-data/get-data?#get_multiple_documents_from_a_collection
    public List<Vet> findByName(String name) {

        ApiFuture<QuerySnapshot> future = firestore.collection("vets").whereEqualTo("name", name).get();
        List<Vet> vets = new ArrayList<>();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for(DocumentSnapshot doc : documents) {
                vets.add(doc.toObject(Vet.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            // TODO implement better error handling
            e.printStackTrace();
        }
        return vets;
    }
}

