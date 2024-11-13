package com.example.passwordmanager.models;

// Modelo que representa una contraseña almacenada en Firestore
public class Password {
    private String passwordId; // ID único de la contraseña
    private String owner; // Propietario de la contraseña (email del usuario)
    private String website; // Sitio web o aplicación asociado
    private String username; // Usuario del sitio o aplicación
    private String password; // Contraseña almacenada
    private String notes; // Notas adicionales

    // Constructor vacío requerido por Firestore
    public Password() {}

    // Constructor para inicializar todos los atributos
    public Password(String passwordId, String owner, String website, String username, String password, String notes) {
        this.passwordId = passwordId;
        this.owner = owner;
        this.website = website;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    // Métodos getter y setter para cada atributo
    public String getPasswordId() { return passwordId; }
    public void setPasswordId(String passwordId) { this.passwordId = passwordId; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
