package com.example.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.passwordmanager.adapters.PasswordAdapter;
import com.example.passwordmanager.models.Password;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Manejo de autenticación de usuario
    private FirebaseFirestore db; // Base de datos Firestore para almacenar las contraseñas
    private RecyclerView recyclerView; // Lista visual de contraseñas
    private PasswordAdapter passwordAdapter; // Adaptador para la lista de contraseñas
    private List<Password> passwordList; // Lista de contraseñas obtenidas
    private Button btnCreatePassword; // Botón para crear una nueva contraseña

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicialización de Firebase y vistas
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnCreatePassword = findViewById(R.id.btnCreatePassword);

        // Configuración de RecyclerView y adaptador
        passwordList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        passwordAdapter = new PasswordAdapter(this, passwordList);
        recyclerView.setAdapter(passwordAdapter);

        // Cargar la lista de contraseñas desde Firestore
        loadPasswordsList();

        // Botón para navegar a la actividad de creación de contraseñas
        btnCreatePassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreatePassword.class);
            ((Activity) this).startActivityForResult(intent, 1); // Código 1 para manejar el resultado
        });
    }

    // Método que maneja los resultados de actividades lanzadas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica si el resultado proviene de la actividad de creación/edición de contraseñas
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String action = data.getStringExtra("action");
                // Si hubo cambios (creación, actualización o eliminación), recargar la lista
                if ("created".equals(action) || "updated".equals(action) || "deleted".equals(action)) {
                    loadPasswordsList();
                }
            }
        }
    }

    // Método para cargar la lista de contraseñas desde Firestore
    private void loadPasswordsList() {
        String userEmail = auth.getCurrentUser().getEmail(); // Obtiene el email del usuario autenticado

        db.collection("passwords")
                .whereEqualTo("owner", userEmail) // Filtra por el dueño de las contraseñas
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordList.clear(); // Limpia la lista antes de actualizar
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Password password = document.toObject(Password.class); // Mapea los datos a objetos Password
                            passwordList.add(password); // Agrega cada contraseña a la lista
                        }
                        passwordAdapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                    } else {
                        // Muestra un mensaje de error si no se pudieron obtener las contraseñas
                        Toast.makeText(this, "Error al actualizar la lista", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}