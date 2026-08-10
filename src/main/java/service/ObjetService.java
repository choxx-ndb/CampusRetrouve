package service;

import dao.ObjetDAO;
import modele.Objet;

import java.util.List;

public class ObjetService {
    private final ObjetDAO objetDAO = new ObjetDAO();

    public void publierAnnonce(Objet objet) {
        validerObjet(objet);
        objet.setStatus("disponible");
        objetDAO.add(objet);
    }

    public List<Objet> recupererTous() {
        return objetDAO.selectAll();
    }

    public List<Objet> recupererParType(String type) {
        if (!"perdue".equals(type) && !"trouve".equals(type)) {
            return recupererTous();
        }
        return objetDAO.findByType(type);
    }

    public Objet recupererParId(int id) {
        return objetDAO.getById(id);
    }

    public void supprimer(int id) {
        objetDAO.delete(id);
    }

    public void changerStatut(int id, String status) {
        if (!"disponible".equals(status) && !"reclame".equals(status) && !"restitue".equals(status)) {
            throw new IllegalArgumentException("Statut objet invalide.");
        }
        objetDAO.updateStatus(id, status);
    }

    private void validerObjet(Objet objet) {
        if (objet == null || isBlank(objet.getTitre()) || isBlank(objet.getType()) || objet.getProprietaireId() <= 0) {
            throw new IllegalArgumentException("Titre, type et propriÃ©taire sont obligatoires.");
        }
        if (!"perdue".equals(objet.getType()) && !"trouve".equals(objet.getType())) {
            throw new IllegalArgumentException("Le type doit Ãªtre 'perdue' ou 'trouve'.");
        }
        if (objet.getTitre().trim().length() < 3) {
            throw new IllegalArgumentException("Le titre doit contenir au moins 3 caractÃ¨res.");
        }
        objet.setTitre(objet.getTitre().trim());
        objet.setDescription(objet.getDescription() == null ? "" : objet.getDescription().trim());
        objet.setLocalisation(objet.getLocalisation() == null ? "" : objet.getLocalisation().trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
