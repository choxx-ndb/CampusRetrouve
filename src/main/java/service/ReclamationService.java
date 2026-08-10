package service;

import dao.ObjetDAO;
import dao.ReclamationDAO;
import modele.MessageReclamation;
import modele.Objet;
import modele.Reclamation;

import java.util.List;

public class ReclamationService {
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();
    private final ObjetDAO objetDAO = new ObjetDAO();

    public void creerReclamation(Reclamation reclamation) {
        if (reclamation == null || reclamation.getObjetId() <= 0 || reclamation.getUtilisateurId() <= 0 || isBlank(reclamation.getMessage())) {
            throw new IllegalArgumentException("Message obligatoire pour crÃ©er une rÃ©clamation.");
        }
        Objet objet = objetDAO.getById(reclamation.getObjetId());
        if (objet == null) {
            throw new IllegalArgumentException("Objet introuvable.");
        }
        if (objet.getProprietaireId() == reclamation.getUtilisateurId()) {
            throw new IllegalArgumentException("Vous ne pouvez pas rÃ©clamer votre propre annonce.");
        }
        if (!"disponible".equals(objet.getStatus())) {
            throw new IllegalArgumentException("Cet objet n'est plus disponible.");
        }
        reclamation.setStatus("en_attente");
        reclamation.setMessage(reclamation.getMessage().trim());
        reclamationDAO.add(reclamation);
        reclamationDAO.ajouterMessage(new MessageReclamation(reclamation.getId(), reclamation.getUtilisateurId(), reclamation.getMessage()));
        objetDAO.updateStatus(objet.getId(), "reclame");
    }

    public List<Reclamation> recupererHistoriqueEtudiant(int utilisateurId) {
        return reclamationDAO.findByUtilisateur(utilisateurId);
    }

    public List<Reclamation> recupererReclamationsRecuesPourUtilisateur(int proprietaireId) {
        return reclamationDAO.findRecuesPourProprietaire(proprietaireId);
    }

    public List<Reclamation> recupererToutes() {
        return reclamationDAO.selectAll();
    }

    public List<Reclamation> recupererParStatut(String status) {
        if (isBlank(status) || "toutes".equals(status)) {
            return recupererToutes();
        }
        return reclamationDAO.findByStatus(status);
    }

    public Reclamation recupererParId(int id) {
        return reclamationDAO.getById(id);
    }

    public List<MessageReclamation> recupererMessagesDiscussion(int reclamationId) {
        return reclamationDAO.getMessagesByReclamation(reclamationId);
    }

    public void enregistrerMessage(int reclamationId, int expediteurId, String contenu) {
        if (reclamationId <= 0 || expediteurId <= 0 || isBlank(contenu)) {
            throw new IllegalArgumentException("Le message ne peut pas Ãªtre vide.");
        }
        Reclamation reclamation = reclamationDAO.getById(reclamationId);
        if (reclamation == null) {
            throw new IllegalArgumentException("RÃ©clamation introuvable.");
        }
        boolean participant = reclamation.getUtilisateurId() == expediteurId || reclamation.getProprietaireId() == expediteurId;
        if (!participant) {
            throw new IllegalArgumentException("AccÃ¨s refusÃ© Ã  cette discussion.");
        }
        reclamationDAO.ajouterMessage(new MessageReclamation(reclamationId, expediteurId, contenu.trim()));
    }

    public void traiterReclamation(int reclamationId, int proprietaireId, String decision) {
        Reclamation reclamation = reclamationDAO.getById(reclamationId);
        if (reclamation == null) {
            throw new IllegalArgumentException("Réclamation introuvable.");
        }
        if (reclamation.getProprietaireId() != proprietaireId) {
            throw new IllegalArgumentException("Seul le propriétaire de l'annonce peut traiter cette réclamation.");
        }
        if (!"approuve".equals(decision) && !"rejete".equals(decision)) {
            throw new IllegalArgumentException("Décision invalide.");
        }
        reclamation.setStatus(decision);
        reclamationDAO.update(reclamation);
        objetDAO.updateStatus(reclamation.getObjetId(), "approuve".equals(decision) ? "restitue" : "disponible");
    }

    public boolean peutVoirDiscussion(Reclamation reclamation, int userId, String role) {
        if (reclamation == null) {
            return false;
        }
        return "admin".equals(role) || reclamation.getUtilisateurId() == userId || reclamation.getProprietaireId() == userId;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
