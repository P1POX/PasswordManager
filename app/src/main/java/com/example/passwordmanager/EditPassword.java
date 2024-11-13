package com.example.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.passwordmanager.models.Password;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPassword extends AppCompatActivity {

    private String passwordId;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etWebsite, etUsername, etPassword, etNotes;
    private Button btnBack, btnUpdatePassword, btnDeletePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);

        passwordId = getIntent().getStringExtra("passwordId");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etWebsite = findViewById(R.id.etWebsite);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNotes = findViewById(R.id.etNotes);
        btnBack = findViewById(R.id.btnBack);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnDeletePassword = findViewById(R.id.btnDeletePassword);

        db.collection("passwords").document(passwordId).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Password password = documentSnapshot.toObject(Password.class);
                        etWebsite.setText(password.getWebsite());
                        etUsername.setText(password.getUsername());
                        etPassword.setText(password.getPassword());
                        etNotes.setText(password.getNotes());
                    }
                });

        btnBack.setOnClickListener(view -> {
            finish();
        });

        btnUpdatePassword.setOnClickListener(view -> {
            updatePassword();
        });

        btnDeletePassword.setOnClickListener(view -> {
            deletePassword();
        });
    }

    private void updatePassword() {
        String website = etWebsite.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(website) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String owner = auth.getCurrentUser().getEmail();

        Password pass = new Password(passwordId, owner, website, username, password, notes);
        db.collection("passwords").document(passwordId).set(pass)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "updated");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void deletePassword() {
        db.collection("passwords").document(passwordId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contraseña eliminada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "deleted");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                });
    }
}
