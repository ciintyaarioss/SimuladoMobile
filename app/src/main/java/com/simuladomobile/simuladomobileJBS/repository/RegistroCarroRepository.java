package com.simuladomobile.simuladomobileJBS.repository;

import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;

public class RegistroCarroRepository extends FirestoreRepository<RegistroCarro> {
    public static final String collectionName = "registroCarro";

    public RegistroCarroRepository() {
        super(RegistroCarro.class);
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }
}
