package service;

import dao.UtilisateurDAO;
import modele.Utilisateur;

public class UtilisateurService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur authentifier(String email, String motdepass) {
        if (isBlank(email) || isBlank(motdepass)) {
            return null;
        }
        return utilisateurDAO.authentifier(email.trim().toLowerCase(), motdepass);
    }

    public void inscrire(Utilisateur utilisateur) {
        validerInscription(utilisateur);
        utilisateur.setEmail(utilisateur.getEmail().trim().toLowerCase());
        utilisateur.setNom(utilisateur.getNom().trim());
        utilisateur.setRole("user");
        utilisateurDAO.add(utilisateur);
    }

    public Utilisateur recupererParId(int id) {
        return utilisateurDAO.getById(id);
    }

    public boolean emailExiste(String email) {
        return utilisateurDAO.emailExiste(email.trim().toLowerCase());
    }

    private void validerInscription(Utilisateur utilisateur) {
        if (utilisateur == null || isBlank(utilisateur.getNom()) || isBlank(utilisateur.getEmail()) || isBlank(utilisateur.getMotdepass())) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        }
        if (!utilisateur.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email invalide.");
        }
        if (utilisateur.getMotdepass().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caracteres.");
        }
        if (utilisateurDAO.emailExiste(utilisateur.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Cet email est déja  utilisé.");
        }
        if (utilisateurDAO.nomExiste(utilisateur.getNom().trim())) {
            throw new IllegalArgumentException("Ce nom est déja  utilisés.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
