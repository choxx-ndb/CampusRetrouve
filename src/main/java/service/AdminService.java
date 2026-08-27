package service;

import dao.ObjetDAO;
import dao.ReclamationDAO;
import dao.UtilisateurDAO;
import modele.Objet;
import modele.Reclamation;
import modele.Utilisateur;
import repository.ObjetRepository;
import repository.ReclamationRepository;
import repository.UtilisateurRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final ObjetRepository objetRepository;
    private final ReclamationRepository reclamationRepository;

    public AdminService() {
        this(
                new UtilisateurDAO(),
                new ObjetDAO(),
                new ReclamationDAO()
        );
    }

    public AdminService(
            UtilisateurRepository utilisateurRepository,
            ObjetRepository objetRepository,
            ReclamationRepository reclamationRepository) {

        this.utilisateurRepository = Objects.requireNonNull(
                utilisateurRepository,
                "utilisateurRepository ne peut pas être null"
        );

        this.objetRepository = Objects.requireNonNull(
                objetRepository,
                "objetRepository ne peut pas être null"
        );

        this.reclamationRepository = Objects.requireNonNull(
                reclamationRepository,
                "reclamationRepository ne peut pas être null"
        );
    }

    public Map<String, Integer> getStatistiquesGlobales() {
        Map<String, Integer> stats = new HashMap<>();

        stats.put(
                "utilisateurs",
                utilisateurRepository.countAll()
        );

        stats.put(
                "admins",
                utilisateurRepository.countAdmins()
        );

        stats.put(
                "objets",
                objetRepository.countAll()
        );

        stats.put(
                "reclamationsEnAttente",
                reclamationRepository.countEnAttente()
        );

        return stats;
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.selectAll();
    }

    public List<Objet> listerObjets() {
        return objetRepository.selectAll();
    }

    public List<Reclamation> listerReclamations() {
        return reclamationRepository.selectAll();
    }

    public void promouvoirEnAdmin(int utilisateurId) {
        utilisateurRepository.promouvoirEnAdmin(utilisateurId);
    }

    public void retrograderEnUser(
            int utilisateurId,
            int adminCourantId) {

        if (utilisateurId == adminCourantId) {

            throw new IllegalArgumentException(
                    "Vous ne pouvez pas "
                            + "rétrograder votre propre compte."
            );
        }

        utilisateurRepository
                .retrograderEnUser(
                        utilisateurId
                );
    }

    public void supprimerUtilisateur(
            int utilisateurId,
            int adminCourantId) {

        if (utilisateurId == adminCourantId) {
            throw new IllegalArgumentException(
                    "Vous ne pouvez pas supprimer votre propre compte."
            );
        }

        utilisateurRepository.delete(utilisateurId);
    }

    public void supprimerAnnonce(int objetId) {
        objetRepository.delete(objetId);
    }
}
