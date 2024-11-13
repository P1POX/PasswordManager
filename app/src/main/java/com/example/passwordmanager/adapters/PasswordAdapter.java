package com.example.passwordmanager.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.passwordmanager.EditPassword;
import com.example.passwordmanager.R;
import com.example.passwordmanager.models.Password;
import java.util.List;

// Adaptador para mostrar las contraseñas en un RecyclerView
public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

    private Context context; // Contexto de la actividad o fragmento que utiliza el RecyclerView
    private List<Password> passwordList; // Lista de contraseñas que se mostrarán

    // Constructor para inicializar el adaptador
    public PasswordAdapter(Context context, List<Password> passwordList) {
        this.context = context;
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de cada ítem del RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.password_card, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        // Obtiene la contraseña en la posición actual
        Password password = passwordList.get(position);

        // Configura los datos en los TextViews del diseño
        holder.tvWebsite.setText(password.getWebsite());
        holder.tvUsername.setText("Usuario: " + password.getUsername());
        holder.tvNotes.setText("Notas adicionales: " + password.getNotes());

        // Configura el evento onClick del botón para mostrar la contraseña
        holder.btnShow.setOnClickListener(view -> {
            showPassword(password.getPasswordId()); // Llama al método para mostrar la contraseña
        });
    }

    @Override
    public int getItemCount() {
        // Devuelve el número total de ítems en la lista
        return passwordList.size();
    }

    // Método para autenticar con huella y redirigir a la pantalla de edición
    private void showPassword(String passwordId) {
        // Verifica si el dispositivo soporta autenticación biométrica
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt biometricPrompt;
            BiometricPrompt.PromptInfo promptInfo;

            // Configuración del diálogo de autenticación biométrica
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticación biométrica")
                    .setSubtitle("Usa tu huella para ver la contraseña")
                    .setNegativeButtonText("Cancelar")
                    .build();

            // Configura el manejador de autenticación
            biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                    ContextCompat.getMainExecutor(context),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            // Si la autenticación es exitosa, redirige a EditPassword
                            Intent intent = new Intent(context, EditPassword.class);
                            intent.putExtra("passwordId", passwordId);
                            ((Activity) context).startActivityForResult(intent, 1);
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            // Muestra un mensaje si ocurre un error en la autenticación
                            Toast.makeText(context, "Error: " + errString, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            // Muestra un mensaje si la autenticación falla
                            Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Inicia el proceso de autenticación biométrica
            biometricPrompt.authenticate(promptInfo);
        } else {
            // Si el dispositivo no soporta autenticación biométrica, muestra un mensaje
            Toast.makeText(context, "La autenticación biométrica no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    // Clase interna para representar cada ítem en el RecyclerView
    public static class PasswordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWebsite, tvUsername, tvNotes; // TextViews para mostrar datos de la contraseña
        Button btnShow; // Botón para mostrar la contraseña

        // Constructor que enlaza las vistas del diseño
        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWebsite = itemView.findViewById(R.id.tvWebsite);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnShow = itemView.findViewById(R.id.btnShow);
        }
    }
}