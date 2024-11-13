package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Autenticación con Firebase
    private EditText etEmail, etPassword; // Campos de entrada para email y contraseña
    private Button btnRegister, btnLogin; // Botones para registrar e iniciar sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de FirebaseAuth y vistas
        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene datos de los campos y verifica que no estén vacíos
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    registerUser(email, password); // Llama al método para registrar usuario
                } else {
                    // Muestra un mensaje si los campos están vacíos
                    Toast.makeText(MainActivity.this, "Ingrese sus Datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene datos de los campos y verifica que no estén vacíos
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signIn(email, password); // Llama al método para iniciar sesión
                } else {
                    // Muestra un mensaje si los campos están vacíos
                    Toast.makeText(MainActivity.this, "Ingrese sus Datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para registrar un usuario con FirebaseAuth
    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si el registro es exitoso, muestra el email del usuario registrado
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "User: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Si falla, muestra un mensaje de error
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Método para iniciar sesión con FirebaseAuth
    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->  {
                    if (task.isSuccessful()) {
                        // Limpia los campos de entrada
                        etEmail.setText("");
                        etPassword.setText("");

                        // Redirige a la pantalla principal (HomeActivity)
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Finaliza la actividad actual
                    } else {
                        // Si falla, muestra un mensaje de error
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}