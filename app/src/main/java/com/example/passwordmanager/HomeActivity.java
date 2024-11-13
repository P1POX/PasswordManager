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

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PasswordAdapter passwordAdapter;
    private List<Password> passwordList;
    private Button btnCreatePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCreatePassword = findViewById(R.id.btnCreatePassword);

        passwordList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        passwordAdapter = new PasswordAdapter(this, passwordList);
        recyclerView.setAdapter(passwordAdapter);

        loadPasswordsList();

        btnCreatePassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreatePassword.class);
            ((Activity) this).startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String action = data.getStringExtra("action");
                if ("created".equals(action) || "updated".equals(action) || "deleted".equals(action)) {
                    loadPasswordsList();
                }
            }
        }
    }

    private void loadPasswordsList() {
        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("passwords")
                .whereEqualTo("owner", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Password password = document.toObject(Password.class);
                            passwordList.add(password);
                        }
                        passwordAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al actualizar la lista", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}