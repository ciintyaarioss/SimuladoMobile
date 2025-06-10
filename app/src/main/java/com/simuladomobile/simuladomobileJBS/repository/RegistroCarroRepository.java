package com.simuladomobile.simuladomobileJBS.repository;

import com.google.android.gms.tasks.Task;
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

    public Task<Void> updateByPlaca(String placa, RegistroCarro registroCarro) {
        return super.update("placa", placa, registroCarro);
    }
}
