package com.simuladomobile.simuladomobileJBS.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.simuladomobile.simuladomobileJBS.R;

public class PasswordDialogFragment extends DialogFragment {

    private Runnable onPasswordCorrect;

    public PasswordDialogFragment() {
    }

    public static PasswordDialogFragment newInstance(Runnable onPasswordCorrect) {
        PasswordDialogFragment fragment = new PasswordDialogFragment();
        fragment.onPasswordCorrect = onPasswordCorrect;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_admin_password, null);
        builder.setView(view);

        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etAdminPassword = view.findViewById(R.id.etAdminPassword);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> {
            String senha = etAdminPassword.getText().toString().trim();

            if (senha.isEmpty()) {
                Toast.makeText(getContext(), "Digite a senha", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .whereEqualTo("isAdmin", true)
                    .whereEqualTo("senha", senha)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            dismiss();
                            if (onPasswordCorrect != null) {
                                onPasswordCorrect.run();
                            }
                        } else {
                            Toast.makeText(getContext(), "Senha incorreta", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnConfirm.setOnClickListener(v -> {
            dismiss();
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }
}