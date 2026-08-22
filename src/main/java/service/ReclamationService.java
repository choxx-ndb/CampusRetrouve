package service;

import java.util.List;
import java.util.Objects;

import dao.ObjetDAO;
import dao.ReclamationDAO;
import modele.MessageReclamation;
import modele.Objet;
import modele.Reclamation;
import repository.ObjetRepository;
import repository.ReclamationRepository;

public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final ObjetRepository objetRepository;

    public ReclamationService() {
        this(new ReclamationDAO(), new ObjetDAO());
    }

    public ReclamationService(
            ReclamationRepository reclamationRepository,
            ObjetRepository objetRepository) {
        this.reclamationRepository = Objects.requireNonNull(
                reclamationRepository,
                "reclamationRepository ne peut pas être null"
        );
        this.objetRepository = Objects.requireNonNull(
                objetRepository,
                "objetRepository ne peut pas être null"
        );
    }
    public void creerReclamation(Reclamation reclamation) {
        if (reclamation == null || reclamation.getObjetId() <= 0 || reclamation.getUtilisateurId() <= 0 || isBlank(reclamation.getMessage())) {
            throw new IllegalArgumentException("Message obligatoire pour crÃ©er une rÃ©clamation.");
        }
        Objet objet = objetRepository.getById(reclamation.getObjetId());
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
        reclamationRepository.add(reclamation);
        reclamationRepository.ajouterMessage(new MessageReclamation(reclamation.getId(), reclamation.getUtilisateurId(), reclamation.getMessage()));
        objetRepository.updateStatus(objet.getId(), "reclame");
    }

    public List<Reclamation> recupererHistoriqueEtudiant(int utilisateurId) {
        return reclamationRepository.findByUtilisateur(utilisateurId);
    }

    public List<Reclamation> recupererReclamationsRecuesPourUtilisateur(int proprietaireId) {
        return reclamationRepository.findRecuesPourProprietaire(proprietaireId);
    }

    public List<Reclamation> recupererToutes() {
        return reclamationRepository.selectAll();
    }

    public List<Reclamation> recupererParStatut(String status) {
        if (isBlank(status) || "toutes".equals(status)) {
            return recupererToutes();
        }
        return reclamationRepository.findByStatus(status);
    }

    public Reclamation recupererParId(int id) {
        return reclamationRepository.getById(id);
    }

    public List<MessageReclamation> recupererMessagesDiscussion(int reclamationId) {
        return reclamationRepository.getMessagesByReclamation(reclamationId);
    }

    public void enregistrerMessage(int reclamationId, int expediteurId, String contenu) {
        if (reclamationId <= 0 || expediteurId <= 0 || isBlank(contenu)) {
            throw new IllegalArgumentException("Le message ne peut pas Ãªtre vide.");
        }
        Reclamation reclamation = reclamationRepository.getById(reclamationId);
        if (reclamation == null) {
            throw new IllegalArgumentException("RÃ©clamation introuvable.");
        }
        boolean participant = reclamation.getUtilisateurId() == expediteurId || reclamation.getProprietaireId() == expediteurId;
        if (!participant) {
            throw new IllegalArgumentException("AccÃ¨s refusÃ© Ã  cette discussion.");
        }
        reclamationRepository.ajouterMessage(new MessageReclamation(reclamationId, expediteurId, contenu.trim()));
    }

    public void traiterReclamation(int reclamationId, int proprietaireId, String decision) {
        Reclamation reclamation = reclamationRepository.getById(reclamationId);
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
        reclamationRepository.update(reclamation);
        objetRepository.updateStatus(reclamation.getObjetId(), "approuve".equals(decision) ? "restitue" : "disponible");
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
