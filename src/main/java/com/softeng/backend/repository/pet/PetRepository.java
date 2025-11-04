package com.softeng.backend.repository.pet;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
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
    private static final Logger logger = LoggerFactory.getLogger(PetRepository.class);

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
     * @param pet <code>Pet</code> object to insert into database
     * @return A DTO of the created pet. The content may be empty if <code>pet</code>
     * is invalid
     * @throws ExecutionException if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws DocumentNotFoundException if owner does not exist
     */
    @Override
    public PetDTO createPet(@Valid Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        OwnerDTO owner = ownerRepository.getOwnerById(pet.getOwnerId());
        if (owner.isEmpty())
        {
            throw new DocumentNotFoundException("Owner not found");
        }

        ApiFuture<DocumentReference> addedDocRef = firestore.collection(PET_COLLECTION)
                                                            .add(pet);
        DocumentReference reference = addedDocRef.get();

        return new PetDTO(reference.getId(), reference.get().get().toObject(Pet.class));
    }

    // =========================
    // READ
    // =========================

    /**
     * @param petId Document ID of the pet to retrieve
     * @return A DTO of the pet. The content may be empty if no pet with
     * <code>petId</code> exists
     * @throws ExecutionException if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws DocumentNotFoundException if the pet is not found for provided id
     */
    @Override
    public PetDTO getPetById(String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException {

        ApiFuture<DocumentSnapshot> future = firestore.collection(PET_COLLECTION).document(petId).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Pet pet = document.toObject(Pet.class);
            return new PetDTO(petId, pet);
        } else {
            throw new DocumentNotFoundException("Pet not found");
        }
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
    public List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        List<PetDTO> result = new ArrayList<>();

        OwnerDTO owner = ownerRepository.getOwnerById(ownerId);
        if (owner.isEmpty())
        {
            throw new DocumentNotFoundException("Owner not found");
        }

        ApiFuture<QuerySnapshot> future = firestore.collection(PET_COLLECTION)
                .whereEqualTo("ownerId", ownerId).get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        for (QueryDocumentSnapshot doc: docs) {
            Pet pet = doc.toObject(Pet.class);
            result.add(new PetDTO(doc.getId(), pet));
        }

        return result;
    }


    // =========================
    // UPDATE
    // =========================

    /**
     * @param petId Document ID of the pet to update
     * @param pet   <code>Pet</code> object containing updated information
     * @return The updated <code>Pet</code> object
     * @throws ExecutionException   if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws DocumentNotFoundException if the pet is not found for provided id
     */
    @Override
    public PetDTO updatePet(String petId, @Valid Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        OwnerDTO owner = ownerRepository.getOwnerById(pet.getOwnerId());
        if (owner.isEmpty())
        {
            throw new DocumentNotFoundException("Owner not found");
        }

        ApiFuture<DocumentSnapshot> future = firestore.collection(PET_COLLECTION).document(petId).get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new DocumentNotFoundException("Pet not found");
        }

        DocumentReference docRef = document.getReference();
        ApiFuture<WriteResult> writeResult = docRef.set(pet);
        writeResult.get();

        Pet updatedPet = docRef.get().get().toObject(Pet.class);
        return new PetDTO(petId, updatedPet);
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
     * @throws DocumentNotFoundException if the pet is not found for provided id
     */
    @Override
    public PetDTO deletePet(String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException {

        ApiFuture<DocumentSnapshot> future = firestore.collection(PET_COLLECTION).document(petId).get();
        DocumentSnapshot document = future.get();

        if (!document.exists()) {
            throw new DocumentNotFoundException("Pet not found");
        }

        Pet deletedPet = document.toObject(Pet.class);
        PetDTO result = new PetDTO(document.getId(), deletedPet);

        DocumentReference docRef = document.getReference();
        docRef.delete().get();

        return result;
    }

    // =========================
    // ADD DIARY ENTRY OPERATION
    // =========================
    /**
     * @param petId Document ID of the pet to delete
     * @param diary diary entry to be added
     * @return The newly created <code>DiaryDTO</code> object
     * @throws ExecutionException   if computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws DocumentNotFoundException if no pet with <code>petId</code> exists,
     */
    @Override
    public DiaryDTO addDiaryEntry(@NotNull @NotBlank String petId, @NotNull Diary diary) throws ExecutionException, InterruptedException, DocumentNotFoundException
    {
        DocumentReference docRef = firestore.collection(PET_COLLECTION).document(petId);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            logger.info("DEBUG LOG: Pet not found for id {} in add diary entry operation", petId);
            throw new DocumentNotFoundException("Pet not found for id " + petId);
        }

        // Reference to subcollection "diaries" under the pet document
        ApiFuture<DocumentReference> futureDocRef = firestore.collection("pets")
                .document(petId)
                .collection("diaries")
                .add(diary);
        DocumentReference reference = futureDocRef.get();

        return new DiaryDTO(reference.getId(), reference.get().get().toObject(Diary.class));
    }

    // =========================
    // GET DIARY ENTRY IN RANGE OPERATION
    // =========================
    public List<DiaryDTO> getDiaryEntryInRange(@NotNull @NotBlank String petId,
                                                    @NotNull Date from,
                                                    @NotNull Date to,
                                                    int limit) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        DocumentReference petRef = firestore.collection(PET_COLLECTION).document(petId);
        DocumentSnapshot docSnapshot = petRef.get().get();

        if (!docSnapshot.exists()) {
            logger.info("DEBUG LOG: Pet not found for id {} in get diary entry in range operation", petId);
            throw new DocumentNotFoundException("Pet not found for id " + petId); // or throw exception
        }

        CollectionReference diariesRef = petRef.collection("diaries");

        Query query = diariesRef
                .whereGreaterThanOrEqualTo("createTimestamp", from)
                .whereLessThanOrEqualTo("createTimestamp", to)
                .orderBy("createTimestamp", Query.Direction.ASCENDING)
                .limit(limit);

        QuerySnapshot querySnapshot = query.get().get();

        ArrayList<DiaryDTO> diaries = new ArrayList<>();
        querySnapshot.forEach(doc -> {
            Diary diary = doc.toObject(Diary.class);
            diaries.add(new DiaryDTO(doc.getId(), diary));
        });
        return diaries;
    }
}
