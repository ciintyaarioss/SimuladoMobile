package com.simuladomobile.simuladomobileJBS.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.simuladomobile.simuladomobileJBS.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        findViewById(R.id.btnLogin).setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        if(email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("usuarios")
                .whereEqualTo("email", email)
                .whereEqualTo("senha", senha);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                prefs.edit().putString("user_email", email).apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Erro ao conectar: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }
}
