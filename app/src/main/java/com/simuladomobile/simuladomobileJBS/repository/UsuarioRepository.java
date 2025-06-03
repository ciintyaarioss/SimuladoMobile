package com.simuladomobile.simuladomobileJBS.repository;

import com.simuladomobile.simuladomobileJBS.model.Usuario;

public class UsuarioRepository extends FirestoreRepository<Usuario> {
    public static final String collectionName = "usuarios";

    public UsuarioRepository() {
        super(Usuario.class);
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }
}
