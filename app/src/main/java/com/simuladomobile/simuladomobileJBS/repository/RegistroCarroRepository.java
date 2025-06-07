package com.simuladomobile.simuladomobileJBS.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;

public class RegistroCarroRepository extends FirestoreRepository<RegistroCarro> {
    public static final String collectionName = "registrarCarro";

    public RegistroCarroRepository() {
        super(RegistroCarro.class);
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }

    public Task<DocumentReference> save(RegistroCarro registroCarro) {
        return FirebaseFirestore.getInstance()
                .collection(getCollectionName())
                .add(registroCarro);
    }
}
