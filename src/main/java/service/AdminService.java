package service;

import dao.ObjetDAO;
import dao.ReclamationDAO;
import dao.UtilisateurDAO;
import modele.Objet;
import modele.Reclamation;
import modele.Utilisateur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ObjetDAO objetDAO = new ObjetDAO();
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    public Map<String, Integer> getStatistiquesGlobales() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("utilisateurs", utilisateurDAO.countAll());
        stats.put("admins", utilisateurDAO.countAdmins());
        stats.put("objets", objetDAO.countAll());
        stats.put("reclamationsEnAttente", reclamationDAO.countEnAttente());
        return stats;
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurDAO.selectAll();
    }

    public List<Objet> listerObjets() {
        return objetDAO.selectAll();
    }

    public List<Reclamation> listerReclamations() {
        return reclamationDAO.selectAll();
    }

    public void promouvoirEnAdmin(int utilisateurId) {
        utilisateurDAO.promouvoirEnAdmin(utilisateurId);
    }

    public void retrograderEnUser(int utilisateurId) {
        utilisateurDAO.retrograderEnUser(utilisateurId);
    }

    public void supprimerUtilisateur(int utilisateurId, int adminCourantId) {
        if (utilisateurId == adminCourantId) {
            throw new IllegalArgumentException("Vous ne pouvez pas supprimer votre propre compte.");
        }
        utilisateurDAO.delete(utilisateurId);
    }

    public void supprimerAnnonce(int objetId) {
        objetDAO.delete(objetId);
    }
}
