package com.simuladomobile.simuladomobileJBS.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    public RegistrarCarro() {
    }

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


        repository.makeRealtimeListener((lista, error) -> {
            if (!isAdded() || getContext() == null) {
                return;
            }
            if (error != null) {
                Toast.makeText(getContext(), "Erro ao carregar registros", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lista == null || lista.isEmpty()) {
                Toast.makeText(getContext(), "Nenhum registro encontrado", Toast.LENGTH_SHORT).show();
                return;
            }
            lista.sort((o1, o2) -> o2.getDataEntrada().compareTo(o1.getDataEntrada()));
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                {
                    if (adapter != null) {
                        adapter.updateData(lista);
                    }
                });
            }

        });

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


        repository.save(registro, task ->
            task.addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Entrada registrada com sucesso", Toast.LENGTH_SHORT).show();
                        editTextPlaca.setText("");
                        carregarRegistros();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Erro ao registrar entrada", Toast.LENGTH_SHORT).show())

        );
    }

    private void carregarRegistros() {
        repository.getAll(lista -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                    {
                        lista.sort((o1, o2) -> o2.getDataEntrada().compareTo(o1.getDataEntrada()));
                        adapter.updateData(lista);
                    });
                }
            },
            error -> Toast.makeText(getContext(), "Erro ao carregar registros", Toast.LENGTH_SHORT).show()
        );

    }

    private void onSaidaClick(RegistroCarro registro) {
        if (registro.getDataSaida() != null) {
            Toast.makeText(getContext(), "Saída já registrada", Toast.LENGTH_SHORT).show();
            return;
        }

        customDialogConfirmar(
                "Registrar saída",
                "Deseja indicar a saída do veículo?",
                () -> {
                    registro.setDataSaida(new Date());
                    repository.updateByPlaca(registro.getPlaca(), registro)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Saída registrada", Toast.LENGTH_SHORT).show();
                                carregarRegistros();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Erro ao registrar saída", Toast.LENGTH_SHORT).show());
                },
                null
        );
    }

    private void customDialogConfirmar(String titulo, String mensagem, Runnable confirmarAction, Runnable cancelarAction) {
        Dialog caixaAlert = new Dialog(getContext());
        caixaAlert.setContentView(R.layout.dialog_confirm_register);

        if (caixaAlert.getWindow() != null) {
            caixaAlert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            caixaAlert.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        caixaAlert.setCancelable(false);

        TextView tituloAlert = caixaAlert.findViewById(R.id.titulo);
        TextView mensagemAlert = caixaAlert.findViewById(R.id.mensagem);
        Button confirmarAlert = caixaAlert.findViewById(R.id.confirmar);
        Button cancelarAlert = caixaAlert.findViewById(R.id.ok);

        tituloAlert.setText(titulo);
        mensagemAlert.setText(mensagem);

        confirmarAlert.setOnClickListener(view -> {
            caixaAlert.dismiss();
            if (confirmarAction != null) {
                confirmarAction.run();
            }
        });

        cancelarAlert.setOnClickListener(view -> {
            caixaAlert.dismiss();
            if (cancelarAction != null) {
                cancelarAction.run();
            }
        });

        caixaAlert.show();
    }

}
