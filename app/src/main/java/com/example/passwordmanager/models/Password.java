package com.example.passwordmanager.models;

public class Password {
    private String passwordId;
    private String owner;
    private String website;
    private String username;
    private String password;
    private String notes;

    public Password() {}

    public Password(String passwordId, String owner, String website, String username, String password, String notes) {
        this.passwordId = passwordId;
        this.owner = owner;
        this.website = website;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

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
