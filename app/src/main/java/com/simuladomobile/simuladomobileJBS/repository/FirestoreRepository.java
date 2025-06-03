package com.simuladomobile.simuladomobileJBS.repository;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class FirestoreRepository<T extends Serializable> {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final Class<T> clazz;

    public FirestoreRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract String getCollectionName();

    protected CollectionReference getCollection() {
        return db.collection(this.getCollectionName());
    }

    public void save(T object, Consumer<Task<?>> receiver) {
        Task<?> task = getCollection().document().set(object);
        receiver.accept(task);
    }

    public void save(T object) {
        save(object, result -> {});
    }

    public void delete(String documentId, Consumer<Task<?>> receiver) {
        Task<?> task = getCollection().document(documentId).delete();
        receiver.accept(task);
    }

    public void delete(String documentId) {
        delete(documentId, result -> {});
    }

    public void getAll(Consumer<List<T>> receiver) {
        getCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                receiver.accept(task.getResult().toObjects(this.clazz));
            } else {
                receiver.accept(null);
            }
        });
    }

    public void getById(String documentId, Consumer<T> receiver) {
        getCollection().document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                receiver.accept(task.getResult().toObject(this.clazz));
            } else {
                receiver.accept(null);
            }
        });
    }

    @FunctionalInterface
    public interface RealtimeListener<T extends Serializable> {
        void onDataChanged(@Nullable List<T> data, @Nullable Exception error);
    }

    private EventListener<QuerySnapshot> getListener(RealtimeListener<T> receiver) {
        return (value, error) -> {
            if (error != null) {
                System.err.println("Listen failed: " + error.getMessage());
                return;
            }

            if (value == null) {
                return;
            }

            List<T> data = null;
            Exception passingException = null;
            try {
                data = value
                        .getDocuments()
                        .stream()
                        .map(doc -> doc.toObject(this.clazz))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                passingException = e;
            }

            receiver.onDataChanged(data, passingException);
        };
    }

    public void makeRealtimeListener(RealtimeListener<T> realtimeListener) {
        getCollection()
                .addSnapshotListener(getListener(realtimeListener));
    }
}
