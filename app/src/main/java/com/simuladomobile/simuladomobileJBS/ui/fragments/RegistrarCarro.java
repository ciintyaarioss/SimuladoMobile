package com.simuladomobile.simuladomobileJBS.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.simuladomobile.simuladomobileJBS.R;
import com.simuladomobile.simuladomobileJBS.adapter.RegistroCarroAdapter;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;
import com.simuladomobile.simuladomobileJBS.repository.RegistroCarroRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrarCarro extends Fragment {

    private EditText editTextPlaca;
    private Button buttonRegistrarEntrada;
    private RecyclerView recyclerView;
    private RegistroCarroAdapter adapter;
    private List<RegistroCarro> registroCarroList;
    private RegistroCarroRepository repository;

    private String usuarioEmail;

    public RegistrarCarro() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_registrar_carro, container, false);

        editTextPlaca = view.findViewById(R.id.editTextPlaca);
        buttonRegistrarEntrada = view.findViewById(R.id.buttonRegistrarEntrada);
        recyclerView = view.findViewById(R.id.recyclerViewRegistros);

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        usuarioEmail = prefs.getString("user_email", "Desconhecido");

        repository = new RegistroCarroRepository();
        registroCarroList = new ArrayList<>();
        adapter = new RegistroCarroAdapter(registroCarroList, this::onSaidaClick);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonRegistrarEntrada.setOnClickListener(v -> registrarEntrada());

        carregarRegistros();

        return view;
    }

    private void registrarEntrada() {
        String placa = editTextPlaca.getText().toString().toUpperCase().trim();

        if (TextUtils.isEmpty(placa)) {
            Toast.makeText(getContext(), "Digite a placa do veículo", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroCarro registro = new RegistroCarro();
        registro.setPlaca(placa);
        registro.setDataEntrada(new Date());
        registro.setUsuarioEmail(usuarioEmail);

        repository.save(registro)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Entrada registrada com sucesso", Toast.LENGTH_SHORT).show();
                    editTextPlaca.setText("");
                    carregarRegistros();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erro ao registrar entrada", Toast.LENGTH_SHORT).show());
    }

    private void carregarRegistros() {
        FirebaseFirestore.getInstance()
                .collection(RegistroCarroRepository.collectionName)
                .orderBy("dataEntrada", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    registroCarroList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        RegistroCarro registro = doc.toObject(RegistroCarro.class);
                        registroCarroList.add(registro);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erro ao carregar registros", Toast.LENGTH_SHORT).show());

    }

    private void onSaidaClick(RegistroCarro registro) {
        if (registro.getDataSaida() != null) {
            Toast.makeText(getContext(), "Saída já registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Registrar saída")
                .setMessage("Deseja indicar a saída do veículo?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    registro.setDataSaida(new Date());
                    repository.update(registro.getPlaca(), registro)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Saída registrada", Toast.LENGTH_SHORT).show();
                                carregarRegistros();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Erro ao registrar saída", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
