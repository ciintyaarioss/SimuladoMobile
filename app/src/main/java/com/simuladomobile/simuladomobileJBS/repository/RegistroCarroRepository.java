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

    public Task<Void> update(String placa, RegistroCarro registroCarro) {
        return FirebaseFirestore.getInstance()
                .collection(getCollectionName())
                .whereEqualTo("placa", placa)
                .limit(1)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful() || task.getResult().isEmpty()) {
                        throw task.getException();
                    }
                    String docId = task.getResult().getDocuments().get(0).getId();
                    return FirebaseFirestore.getInstance()
                            .collection(getCollectionName())
                            .document(docId)
                            .set(registroCarro);
                });
    }
}
