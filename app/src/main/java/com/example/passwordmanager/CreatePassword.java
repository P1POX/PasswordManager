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

import java.util.UUID;

public class CreatePassword extends AppCompatActivity {

    private FirebaseAuth auth; // Manejo de autenticación de usuario
    private FirebaseFirestore db; // Base de datos Firestore para almacenar contraseñas
    private EditText etWebsite, etUsername, etPassword, etNotes; // Campos de entrada para los datos de la contraseña
    private Button btnBack, btnCreatePassword; // Botones para regresar y crear la contraseña

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password);

        // Inicialización de Firebase y vistas
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etWebsite = findViewById(R.id.etWebsite);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNotes = findViewById(R.id.etNotes);
        btnBack = findViewById(R.id.btnBack);
        btnCreatePassword = findViewById(R.id.btnCreatePassword);

        // Botón para regresar a la actividad anterior
        btnBack.setOnClickListener(view -> {
            finish(); // Finaliza la actividad actual
        });

        // Botón para crear una nueva contraseña
        btnCreatePassword.setOnClickListener(view -> {
            createPassword(); // Llama al método para guardar la contraseña
        });
    }

    // Método para crear y guardar una nueva contraseña en Firestore
    private void createPassword() {
        // Obtiene los datos ingresados por el usuario
        String website = etWebsite.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Verifica que los campos requeridos no estén vacíos
        if (TextUtils.isEmpty(website) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Genera un ID único para la contraseña y obtiene el email del usuario autenticado
        String passwordId = UUID.randomUUID().toString();
        String owner = auth.getCurrentUser().getEmail();

        // Crea un objeto Password con los datos ingresados
        Password pass = new Password(passwordId, owner, website, username, password, notes);

        // Guarda la contraseña en Firestore
        db.collection("passwords").document(passwordId).set(pass)
                .addOnSuccessListener(aVoid -> {
                    // Si el guardado es exitoso, muestra un mensaje y envía el resultado a la actividad anterior
                    Toast.makeText(this, "Contraseña guardada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "created"); // Indica que se creó una nueva contraseña
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish(); // Finaliza la actividad actual
                })
                .addOnFailureListener(e -> {
                    // Muestra un mensaje de error si el guardado falla
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }
}
