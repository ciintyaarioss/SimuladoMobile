package com.simuladomobile.simuladomobileJBS.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simuladomobile.simuladomobileJBS.R;
import com.simuladomobile.simuladomobileJBS.model.RegistroCarro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroCarroAdapter extends RecyclerView.Adapter<RegistroCarroAdapter.ViewHolder> {

    public interface OnSaidaClickListener {
        void onSaidaClick(RegistroCarro registroCarro);
    }

    private List<RegistroCarro> lista;
    private final OnSaidaClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public RegistroCarroAdapter(List<RegistroCarro> lista, OnSaidaClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_carro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroCarro registro = lista.get(position);

        holder.textPlaca.setText("Placa: " + registro.getPlaca());
        holder.textEntrada.setText("Entrada: " + formatarData(registro.getDataEntrada()));
        holder.textSaida.setText("Saída: " + (registro.getDataSaida() != null ? formatarData(registro.getDataSaida()) : "Não registrada"));

        holder.btnRegistrarSaida.setVisibility(registro.getDataSaida() == null ? View.VISIBLE : View.GONE);

        holder.btnRegistrarSaida.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaidaClick(registro);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPlaca, textEntrada, textSaida;
        Button btnRegistrarSaida;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlaca = itemView.findViewById(R.id.textPlaca);
            textEntrada = itemView.findViewById(R.id.textEntrada);
            textSaida = itemView.findViewById(R.id.textSaida);
            btnRegistrarSaida = itemView.findViewById(R.id.btnRegistrarSaida);
        }
    }

    private String formatarData(Date data) {
        return data != null ? dateFormat.format(data) : "Indisponível";
    }
}
