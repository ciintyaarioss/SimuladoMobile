package com.simuladomobile.simuladomobileJBS.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.simuladomobile.simuladomobileJBS.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtSenha;
    private Button btnLogin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);

        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        Query query = db.collection("usuarios")
                .whereEqualTo("email", email)
                .whereEqualTo("senha", senha);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Erro ao conectar: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }
}
