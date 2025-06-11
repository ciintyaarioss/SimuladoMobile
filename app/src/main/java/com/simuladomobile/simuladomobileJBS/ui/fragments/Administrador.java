package com.simuladomobile.simuladomobileJBS.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.simuladomobile.simuladomobileJBS.R;
import com.simuladomobile.simuladomobileJBS.adapter.RegistroCarroAdminAdapter;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;
import com.simuladomobile.simuladomobileJBS.repository.RegistroCarroRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Administrador extends Fragment {

    private RecyclerView recyclerView;
    private RegistroCarroAdminAdapter adminAdapter;
    private List<RegistroCarro> registroCarroLista;
    private RegistroCarroRepository repository;
    private Button buttonExcluir;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Administrador() {
    }

    public static Administrador newInstance(String param1, String param2) {
        Administrador fragment = new Administrador();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_administrador, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRegistros);
        buttonExcluir = view.findViewById(R.id.buttonExcluir);

        repository = new RegistroCarroRepository();
        registroCarroLista = new ArrayList<>();
        adminAdapter = new RegistroCarroAdminAdapter(registroCarroLista, registro -> {

        });

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adminAdapter);

        carregarRegistros();

        buttonExcluir.setOnClickListener(v -> excluirRegistrosDoMesPassado());

        return view;
    }

    private void carregarRegistros() {
        repository.getAll(lista -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                        {
                            lista.sort((o1, o2) -> o2.getDataEntrada().compareTo(o1.getDataEntrada()));
                            adminAdapter.updateData(lista);
                        });
                    }
                },
                error -> Toast.makeText(getContext(), "Erro ao carregar registros", Toast.LENGTH_SHORT).show()
        );
    }

    private void excluirRegistrosDoMesPassado() {
        long mesAntigo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

        FirebaseFirestore.getInstance()
                .collection(RegistroCarroRepository.collectionName)
                .whereLessThan("dataEntrada", new Date(mesAntigo))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }
                    Toast.makeText(getContext(), "Registros antigos excluídos", Toast.LENGTH_SHORT).show();
                    carregarRegistros();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erro ao excluir registros", Toast.LENGTH_SHORT).show());
    }
}