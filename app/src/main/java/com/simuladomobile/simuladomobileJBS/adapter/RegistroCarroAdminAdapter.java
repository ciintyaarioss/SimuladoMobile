package com.simuladomobile.simuladomobileJBS.adapter;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;
import com.simuladomobile.simuladomobileJBS.repository.RegistroCarroRepository;

import java.util.Date;
import java.util.List;

public class RegistroCarroAdminAdapter extends BaseRegistroCarroAdapter {

    public RegistroCarroAdminAdapter(List<RegistroCarro> lista, OnSaidaClickListener listener) {
        super(lista, listener);
    }

    @Override
    public int showRegistrarSaida(RegistroCarro registroCarro) {
        return View.GONE;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        RegistroCarro registro = super.lista.get(position);

        holder.itemView.setOnLongClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(RegistroCarroRepository.collectionName)
                    .whereEqualTo("placa", registro.getPlaca())
                    .whereEqualTo("dataEntrada", registro.getDataEntrada())
                    .get()
                    .addOnCompleteListener(querySnapshot -> {
                        if (querySnapshot.getResult().isEmpty()){
                            Toast.makeText(view.getContext(), "Registro não encontrado!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                         querySnapshot.getResult().getDocuments().get(0).getReference().delete()
                        .addOnSuccessListener(unused -> {
                            // Remove da lista e notifica o adapter
                            super.lista.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, super.lista.size());

                            Toast.makeText(view.getContext(), "Dados Removidos com Sucesso!!!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(view.getContext(), "Erro ao remover do Firestore", Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), "Erro ao Remover Dados!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });
    }


}
