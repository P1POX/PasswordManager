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

    private String passwordId; // ID de la contraseña a editar o eliminar

    private FirebaseAuth auth; // Autenticación de usuario
    private FirebaseFirestore db; // Base de datos Firestore para manejar contraseñas
    private EditText etWebsite, etUsername, etPassword, etNotes; // Campos para los datos de la contraseña
    private Button btnBack, btnUpdatePassword, btnDeletePassword; // Botones para regresar, actualizar y eliminar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);

        // Obtiene el ID de la contraseña desde el Intent
        passwordId = getIntent().getStringExtra("passwordId");

        // Inicialización de Firebase y vistas
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etWebsite = findViewById(R.id.etWebsite);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNotes = findViewById(R.id.etNotes);
        btnBack = findViewById(R.id.btnBack);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnDeletePassword = findViewById(R.id.btnDeletePassword);

        // Carga los datos de la contraseña desde Firestore para mostrarlos en los campos
        db.collection("passwords").document(passwordId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Password password = documentSnapshot.toObject(Password.class);
                etWebsite.setText(password.getWebsite());
                etUsername.setText(password.getUsername());
                etPassword.setText(password.getPassword());
                etNotes.setText(password.getNotes());
            }
        });

        // Botón para regresar a la actividad anterior
        btnBack.setOnClickListener(view -> {
            finish(); // Finaliza la actividad actual
        });

        // Botón para actualizar la contraseña
        btnUpdatePassword.setOnClickListener(view -> {
            updatePassword(); // Llama al método de actualización
        });

        // Botón para eliminar la contraseña
        btnDeletePassword.setOnClickListener(view -> {
            deletePassword(); // Llama al método de eliminación
        });
    }

    // Método para actualizar la contraseña en Firestore
    private void updatePassword() {
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

        // Obtiene el email del usuario autenticado
        String owner = auth.getCurrentUser().getEmail();

        // Crea un objeto Password con los datos ingresados
        Password pass = new Password(passwordId, owner, website, username, password, notes);

        // Actualiza la contraseña en Firestore
        db.collection("passwords").document(passwordId).set(pass)
                .addOnSuccessListener(aVoid -> {
                    // Muestra un mensaje de éxito y envía el resultado a la actividad anterior
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "updated"); // Indica que se actualizó la contraseña
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish(); // Finaliza la actividad actual
                })
                .addOnFailureListener(e -> {
                    // Muestra un mensaje de error si la actualización falla
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para eliminar la contraseña de Firestore
    private void deletePassword() {
        // Elimina el documento de la contraseña en Firestore
        db.collection("passwords").document(passwordId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Muestra un mensaje de éxito y envía el resultado a la actividad anterior
                    Toast.makeText(this, "Contraseña eliminada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "deleted"); // Indica que se eliminó la contraseña
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish(); // Finaliza la actividad actual
                });
    }
}
