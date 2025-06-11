package com.simuladomobile.simuladomobileJBS.adapter;

import android.view.View;

import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;

import java.util.List;

public class RegistroCarroAdminAdapter extends BaseRegistroCarroAdapter {
    public RegistroCarroAdminAdapter(List<RegistroCarro> lista, OnSaidaClickListener listener) {
        super(lista, listener);
    }

    @Override
    public int showRegistrarSaida(RegistroCarro registroCarro) {
        return View.GONE;
    }
}
