package modele;

import java.sql.Timestamp;

public class Reclamation {
    private int id;
    private int objetId;
    private int utilisateurId;
    private String message;
    private String status = "en_attente";
    private Timestamp createdAt;
    private String objetTitre;
    private String demandeurNom;
    private String proprietaireNom;
    private int proprietaireId;

    public Reclamation() {
    }

    public Reclamation(int objetId, int utilisateurId, String message) {
        this.objetId = objetId;
        this.utilisateurId = utilisateurId;
        this.message = message;
        this.status = "en_attente";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjetId() {
        return objetId;
    }

    public void setObjetId(int objetId) {
        this.objetId = objetId;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getObjetTitre() {
        return objetTitre;
    }

    public void setObjetTitre(String objetTitre) {
        this.objetTitre = objetTitre;
    }

    public String getDemandeurNom() {
        return demandeurNom;
    }

    public void setDemandeurNom(String demandeurNom) {
        this.demandeurNom = demandeurNom;
    }

    public String getProprietaireNom() {
        return proprietaireNom;
    }

    public void setProprietaireNom(String proprietaireNom) {
        this.proprietaireNom = proprietaireNom;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }
}
