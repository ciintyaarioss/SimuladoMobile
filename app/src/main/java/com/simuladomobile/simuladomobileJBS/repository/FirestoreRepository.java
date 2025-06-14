package com.simuladomobile.simuladomobileJBS.repository;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public Task<DocumentReference> save(T object) {
        save(object, result -> {});
        return null;
    }

    public void delete(String documentId, Consumer<Task<?>> receiver) {
        Task<?> task = getCollection().document(documentId).delete();
        receiver.accept(task);
    }

    public void delete(String documentId) {
        delete(documentId, result -> {});
    }


    protected T toObject(Task<? extends DocumentSnapshot> task) {
        return task.getResult().toObject(this.clazz);
    }

    protected List<T> toObjectList(Task<? extends QuerySnapshot> task) {
        return task.getResult().toObjects(this.clazz);
    }

    private <S, R> OnCompleteListener<S> buildCommonCompleteListener(
            Consumer<R> receiver,
            Function<Task<? extends S>, R> converter
    ) {
        return task -> {
            if (task.isSuccessful()) {
                receiver.accept(converter.apply(task));
            } else {
                receiver.accept(null);
            }
        };
    }
    public void getAll(Consumer<List<T>> receiver, OnFailureListener onErrorListener) {
        getCollection()
                .get()
                .addOnCompleteListener(buildCommonCompleteListener(receiver, task -> toObjectList(task)))
                .addOnFailureListener(onErrorListener);
    }

    public void getById(String documentId, Consumer<T> receiver) {
        getCollection()
                .document(documentId)
                .get()
                .addOnCompleteListener(buildCommonCompleteListener(receiver, task -> toObject(task)));
    }

    public <V> Task<QuerySnapshot> getByField(String key, V value, Consumer<List<T>> receiver) {
        return getCollection()
                .whereEqualTo(key, value)
                .get()
                .addOnCompleteListener(buildCommonCompleteListener(receiver, task -> toObjectList(task)));

    }

    protected <V> Task<Void> update(String key, V value, T object) throws RuntimeException {
        return FirebaseFirestore.getInstance()
                .collection(getCollectionName())
                .whereEqualTo(key, value)
                .limit(1)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful() || task.getResult().isEmpty()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    String docId = task.getResult().getDocuments().get(0).getId();
                    return FirebaseFirestore.getInstance()
                            .collection(getCollectionName())
                            .document(docId)
                            .set(object);
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
