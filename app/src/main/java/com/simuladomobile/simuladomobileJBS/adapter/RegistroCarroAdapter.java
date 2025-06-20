package com.simuladomobile.simuladomobileJBS.adapter;

import android.view.View;

import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;

import java.util.List;

public class RegistroCarroAdapter extends BaseRegistroCarroAdapter {

    public RegistroCarroAdapter(List<RegistroCarro> lista, OnSaidaClickListener listener) {
        super(lista, listener);
    }

    @Override
    public int showRegistrarSaida(RegistroCarro registroCarro) {
        return registroCarro.getDataSaida() == null ? View.VISIBLE : View.GONE;
    }
}
