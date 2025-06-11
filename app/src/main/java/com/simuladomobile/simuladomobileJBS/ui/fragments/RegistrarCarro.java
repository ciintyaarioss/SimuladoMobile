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
import java.util.concurrent.CompletableFuture;

public class RegistrarCarro extends Fragment {

    // Fields
    private EditText editTextPlaca;
    private Button buttonRegistrarEntrada;
    private RecyclerView recyclerView;
    private RegistroCarroAdapter adapter;
    private List<RegistroCarro> registroCarroList;
    private RegistroCarroRepository repository;
    private String usuarioEmail;

    // Constructor
    public RegistrarCarro() {
    }

    // Lifecycle Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_registrar_carro, container, false);

        initializeViews(view);
        initializeRepository();
        setupRecyclerView();
        setupClickListeners();
        setupRealtimeListener();

        return view;
    }

    // Initialization Methods
    private void initializeViews(View view) {
        editTextPlaca = view.findViewById(R.id.editTextPlaca);
        buttonRegistrarEntrada = view.findViewById(R.id.buttonRegistrarEntrada);
        recyclerView = view.findViewById(R.id.recyclerViewRegistros);

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        usuarioEmail = prefs.getString("user_email", "Desconhecido");
    }

    private void initializeRepository() {
        repository = new RegistroCarroRepository();
    }

    private void setupRecyclerView() {
        registroCarroList = new ArrayList<>();
        adapter = new RegistroCarroAdapter(registroCarroList, this::onSaidaClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        buttonRegistrarEntrada.setOnClickListener(v -> registrarEntrada());
    }

    private void setupRealtimeListener() {
        repository.makeRealtimeListener((lista, error) -> {
            if (!isAdded() || getContext() == null) {
                return;
            }
            if (error != null) {
                showToast("Erro ao carregar registros");
                return;
            }

            if (lista == null || lista.isEmpty()) {
                showToast("Nenhum registro encontrado");
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
    }

    // Main Business Logic - Entry Registration
    private void registrarEntrada() {
        String placa = getPlacaInput();
        if (!isPlacaValid(placa)) return;

        checkVehicleStatusAsync(placa)
                .thenCompose(isParked -> {
                    if (isParked) {
                        showToast("Veículo ja estacionado");
                        return CompletableFuture.completedFuture(null);
                    }
                    return registerVehicleEntryAsync(placa);
                })
                .thenAccept(success -> {
                    if (success != null && success) {
                        onRegistroSaved();
                    }
                })
                .exceptionally(throwable -> {
                    showToast("Erro ao processar entrada");
                    return null;
                });
    }

    private CompletableFuture<Boolean> checkVehicleStatusAsync(String placa) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        repository.getByField("placa", placa, registros -> {
            boolean isParked = registros.stream().anyMatch(registro -> !registro.hasAlreadyExited());
            future.complete(isParked);
        }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    private CompletableFuture<Boolean> registerVehicleEntryAsync(String placa) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        RegistroCarro registro = createRegistro(placa);

        repository.save(registro, task ->
                task.addOnSuccessListener(ref -> future.complete(true))
                        .addOnFailureListener(future::completeExceptionally)
        );

        return future;
    }

    private void onRegistroSaved() {
        showToast("Entrada registrada com sucesso");
        editTextPlaca.setText("");
        carregarRegistros();
    }

    // Exit Registration Logic
    private void onSaidaClick(RegistroCarro registro) {
        if (registro.getDataSaida() != null) {
            showToast("Saída já registrada");
            return;
        }

        customDialogConfirmar(
                "Registrar saída",
                "Deseja indicar a saída do veículo?",
                () -> {
                    registro.setDataSaida(new Date());
                    repository.updateByPlaca(registro.getPlaca(), registro)
                            .addOnSuccessListener(aVoid -> {
                                showToast("Saída registrada");
                                carregarRegistros();
                            })
                            .addOnFailureListener(e -> showToast("Erro ao registrar saída"));
                },
                null
        );
    }

    // Data Operations
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
                error -> showToast("Erro ao carregar registros")
        );
    }

    // Validation Methods
    private String getPlacaInput() {
        return editTextPlaca.getText().toString().toUpperCase().trim();
    }

    private boolean isPlacaValid(String placa) {
        if (TextUtils.isEmpty(placa)) {
            showToast("Digite a placa do veículo");
            return false;
        }
        return true;
    }

    // Object Creation
    private RegistroCarro createRegistro(String placa) {
        RegistroCarro registro = new RegistroCarro();
        registro.setPlaca(placa);
        registro.setDataEntrada(new Date());
        registro.setUsuarioEmail(usuarioEmail);
        return registro;
    }

    // UI Helper Methods
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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