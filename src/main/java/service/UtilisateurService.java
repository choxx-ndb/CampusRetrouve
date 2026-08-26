package service;

import dao.UtilisateurDAO;
import modele.Utilisateur;
import repository.UtilisateurRepository;
import security.BCryptPasswordHasher;
import security.PasswordHasher;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class UtilisateurService {

    private static final int MIN_PASSWORD_LENGTH = 15;
    private static final int BCRYPT_MAX_BYTES = 72;

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordHasher passwordHasher;

    public UtilisateurService() {
        this(
                new UtilisateurDAO(),
                new BCryptPasswordHasher()
        );
    }

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository) {

        this(
                utilisateurRepository,
                new BCryptPasswordHasher()
        );
    }

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            PasswordHasher passwordHasher) {

        this.utilisateurRepository =
                Objects.requireNonNull(
                        utilisateurRepository,
                        "utilisateurRepository ne peut pas être null"
                );

        this.passwordHasher =
                Objects.requireNonNull(
                        passwordHasher,
                        "passwordHasher ne peut pas être null"
                );
    }

    public Utilisateur authentifier(
            String email,
            String motdepass) {

        if (isBlank(email) || isBlank(motdepass)) {
            return null;
        }

        String emailNormalise =
                email.trim().toLowerCase();

        Utilisateur utilisateur =
                utilisateurRepository.findByEmail(
                        emailNormalise
                );

        if (utilisateur == null) {
            return null;
        }

        if (!passwordHasher.matches(
                motdepass,
                utilisateur.getMotdepass())) {
            return null;
        }

        return utilisateur;
    }

    public void inscrire(Utilisateur utilisateur) {

        validerInscription(utilisateur);

        utilisateur.setEmail(
                utilisateur.getEmail()
                        .trim()
                        .toLowerCase()
        );

        utilisateur.setNom(
                utilisateur.getNom().trim()
        );

        utilisateur.setRole("user");

        utilisateur.setMotdepass(
                passwordHasher.hash(
                        utilisateur.getMotdepass()
                )
        );

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

    private void validerInscription(
            Utilisateur utilisateur) {

        if (utilisateur == null
                || isBlank(utilisateur.getNom())
                || isBlank(utilisateur.getEmail())
                || isBlank(utilisateur.getMotdepass())) {

            throw new IllegalArgumentException(
                    "Tous les champs sont obligatoires."
            );
        }

        if (!utilisateur.getEmail()
                .matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )) {

            throw new IllegalArgumentException(
                    "Email invalide."
            );
        }

        String password =
                utilisateur.getMotdepass();

        if (password.length()
                < MIN_PASSWORD_LENGTH) {

            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir "
                            + "au moins 15 caractères."
            );
        }

        if (password
                .getBytes(StandardCharsets.UTF_8)
                .length > BCRYPT_MAX_BYTES) {

            throw new IllegalArgumentException(
                    "Le mot de passe est trop long "
                            + "pour le mécanisme de sécurité utilisé."
            );
        }

        String emailNormalise =
                utilisateur.getEmail()
                        .trim()
                        .toLowerCase();

        String nomNormalise =
                utilisateur.getNom().trim();

        if (utilisateurRepository
                .emailExiste(emailNormalise)) {

            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé."
            );
        }

        if (utilisateurRepository
                .nomExiste(nomNormalise)) {

            throw new IllegalArgumentException(
                    "Ce nom est déjà utilisé."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}
