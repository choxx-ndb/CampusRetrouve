package modele;

import java.sql.Timestamp;

public class Objet {
    private int id;
    private String titre;
    private String description;
    private String type;
    private String localisation;
    private String imagePath;
    private String status = "disponible";
    private int proprietaireId;
    private String proprietaireNom;
    private Timestamp createdAt;

    public Objet() {
    }

    public Objet(String titre, String description, String type, String localisation, String imagePath, int proprietaireId) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.localisation = localisation;
        this.imagePath = imagePath;
        this.proprietaireId = proprietaireId;
        this.status = "disponible";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public String getProprietaireNom() {
        return proprietaireNom;
    }

    public void setProprietaireNom(String proprietaireNom) {
        this.proprietaireNom = proprietaireNom;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

