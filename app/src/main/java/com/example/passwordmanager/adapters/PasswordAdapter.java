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

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

    private Context context;
    private List<Password> passwordList;

    public PasswordAdapter(Context context, List<Password> passwordList) {
        this.context = context;
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.password_card, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        Password password = passwordList.get(position);
        holder.tvWebsite.setText(password.getWebsite());
        holder.tvUsername.setText("Usuario: " + password.getUsername());
        holder.tvNotes.setText("Notas adicionales: " + password.getNotes());

        holder.btnShow.setOnClickListener(view -> {
            showPassword(password.getPasswordId());
        });
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    private void showPassword(String passwordId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt biometricPrompt;
            BiometricPrompt.PromptInfo promptInfo;

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticación biométrica")
                    .setSubtitle("Usa tu huella para ver la contraseña")
                    .setNegativeButtonText("Cancelar")
                    .build();

            biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                    ContextCompat.getMainExecutor(context),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Intent intent = new Intent(context, EditPassword.class);
                            intent.putExtra("passwordId", passwordId);
                            ((Activity) context).startActivityForResult(intent, 1);
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            Toast.makeText(context, "Error: " + errString, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                        }
                    });

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(context, "La autenticación biométrica no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
        }
    }


    public static class PasswordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWebsite, tvUsername, tvNotes;
        Button btnShow;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWebsite = itemView.findViewById(R.id.tvWebsite);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnShow = itemView.findViewById(R.id.btnShow);
        }
    }
}