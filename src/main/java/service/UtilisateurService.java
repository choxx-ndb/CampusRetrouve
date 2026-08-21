package service;

import dao.UtilisateurDAO;
import modele.Utilisateur;
import repository.UtilisateurRepository;

import java.util.Objects;

public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService() {
        this(new UtilisateurDAO());
    }

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = Objects.requireNonNull(
                utilisateurRepository,
                "utilisateurRepository ne peut pas être null"
        );
    }

    public Utilisateur authentifier(String email, String motdepass) {
        if (isBlank(email) || isBlank(motdepass)) {
            return null;
        }

        return utilisateurRepository.authentifier(
                email.trim().toLowerCase(),
                motdepass
        );
    }

    public void inscrire(Utilisateur utilisateur) {
        validerInscription(utilisateur);

        utilisateur.setEmail(
                utilisateur.getEmail().trim().toLowerCase()
        );
        utilisateur.setNom(utilisateur.getNom().trim());
        utilisateur.setRole("user");

        utilisateurRepository.add(utilisateur);
    }

    public Utilisateur recupererParId(int id) {
        return utilisateurRepository.getById(id);
    }

    public boolean emailExiste(String email) {
        return utilisateurRepository.emailExiste(
                email.trim().toLowerCase()
        );
    }

    private void validerInscription(Utilisateur utilisateur) {
        if (utilisateur == null
                || isBlank(utilisateur.getNom())
                || isBlank(utilisateur.getEmail())
                || isBlank(utilisateur.getMotdepass())) {

            throw new IllegalArgumentException(
                    "Tous les champs sont obligatoires."
            );
        }

        if (!utilisateur.getEmail()
                .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException("Email invalide.");
        }

        if (utilisateur.getMotdepass().length() < 6) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins 6 caracteres."
            );
        }

        String emailNormalise =
                utilisateur.getEmail().trim().toLowerCase();
        String nomNormalise = utilisateur.getNom().trim();

        if (utilisateurRepository.emailExiste(emailNormalise)) {
            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé."
            );
        }

        if (utilisateurRepository.nomExiste(nomNormalise)) {
            throw new IllegalArgumentException(
                    "Ce nom est déjà utilisé."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
