package modele;

import java.sql.Timestamp;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motdepass;
    private String role = "user";
    private Timestamp createdAt;

    public Utilisateur() {
    }

    public Utilisateur(String nom, String email, String motdepass) {
        this.nom = nom;
        this.email = email;
        this.motdepass = motdepass;
        this.role = "user";
    }

    public Utilisateur(int id, String nom, String email, String motdepass, String role, Timestamp createdAt) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motdepass = motdepass;
        setRole(role);
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotdepass() {
        return motdepass;
    }

    public void setMotdepass(String motdepass) {
        this.motdepass = motdepass;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (!"admin".equals(role) && !"user".equals(role)) {
            this.role = "user";
            return;
        }
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
