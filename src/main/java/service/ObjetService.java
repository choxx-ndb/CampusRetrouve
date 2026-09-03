package service;

import java.util.List;
import java.util.Objects;

import dao.ObjetDAO;
import modele.Objet;
import repository.ObjetRepository;

public class ObjetService {

    private static final int MIN_TITLE_LENGTH = 3;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MAX_LOCATION_LENGTH = 255;

    private final ObjetRepository objetRepository;

    public ObjetService() {
        this(new ObjetDAO());
    }

    public ObjetService(
            ObjetRepository objetRepository) {

        this.objetRepository =
                Objects.requireNonNull(
                        objetRepository,
                        "objetRepository ne peut pas être null"
                );
    }

    public void publierAnnonce(Objet objet) {

        validerEtNormaliser(objet);

        objet.setStatus("disponible");

        objetRepository.add(objet);
    }

    public List<Objet> recupererTous() {
        return objetRepository.selectAll();
    }

    public List<Objet> recupererParType(String type) {

        if (!"perdue".equals(type)
                && !"trouve".equals(type)) {

            return recupererTous();
        }

        return objetRepository.findByType(type);
    }

    public List<Objet> recupererParProprietaire(
            int proprietaireId) {

        if (proprietaireId <= 0) {
            throw new IllegalArgumentException(
                    "Utilisateur invalide."
            );
        }

        return objetRepository.findByUserId(
                proprietaireId
        );
    }

    public Objet recupererParId(int id) {
        return objetRepository.getById(id);
    }

    public Objet recupererAnnoncePourModification(
            int objetId,
            int utilisateurId) {

        Objet objet =
                recupererAnnoncePossedee(
                        objetId,
                        utilisateurId
                );

        verifierDisponiblePourGestion(objet);

        return objet;
    }

    public void modifierAnnonce(
            int objetId,
            int utilisateurId,
            String titre,
            String description,
            String type,
            String localisation) {

        Objet objet =
                recupererAnnoncePossedee(
                        objetId,
                        utilisateurId
                );

        verifierDisponiblePourGestion(objet);

        objet.setTitre(titre);
        objet.setDescription(description);
        objet.setType(type);
        objet.setLocalisation(localisation);

        validerEtNormaliser(objet);

        objetRepository.updateContent(objet);
    }

    public void supprimerAnnonce(
            int objetId,
            int utilisateurId) {

        Objet objet =
                recupererAnnoncePossedee(
                        objetId,
                        utilisateurId
                );

        verifierDisponiblePourGestion(objet);

        objetRepository.delete(objetId);
    }

    public void supprimer(int id) {
        objetRepository.delete(id);
    }

    public void changerStatut(
            int id,
            String status) {

        if (!"disponible".equals(status)
                && !"reclame".equals(status)
                && !"restitue".equals(status)) {

            throw new IllegalArgumentException(
                    "Statut objet invalide."
            );
        }

        objetRepository.updateStatus(
                id,
                status
        );
    }

    private Objet recupererAnnoncePossedee(
            int objetId,
            int utilisateurId) {

        if (objetId <= 0
                || utilisateurId <= 0) {

            throw new IllegalArgumentException(
                    "Annonce introuvable ou accès refusé."
            );
        }

        Objet objet =
                objetRepository.getById(objetId);

        if (objet == null
                || objet.getProprietaireId()
                        != utilisateurId) {

            throw new IllegalArgumentException(
                    "Annonce introuvable ou accès refusé."
            );
        }

        return objet;
    }

    private void verifierDisponiblePourGestion(
            Objet objet) {

        if (!"disponible".equals(
                objet.getStatus())) {

            throw new IllegalArgumentException(
                    "Seule une annonce disponible "
                            + "peut être modifiée "
                            + "ou supprimée."
            );
        }
    }

    private void validerEtNormaliser(
            Objet objet) {

        if (objet == null) {
            throw new IllegalArgumentException(
                    "L'annonce est obligatoire."
            );
        }

        String titre =
                normaliser(objet.getTitre());

        String description =
                normaliser(objet.getDescription());

        String type =
                normaliser(objet.getType());

        String localisation =
                normaliser(objet.getLocalisation());

        if (titre.isEmpty()
                || type.isEmpty()
                || objet.getProprietaireId() <= 0) {

            throw new IllegalArgumentException(
                    "Titre, type et propriétaire "
                            + "sont obligatoires."
            );
        }

        if (!"perdue".equals(type)
                && !"trouve".equals(type)) {

            throw new IllegalArgumentException(
                    "Le type doit être "
                            + "'perdue' ou 'trouve'."
            );
        }

        if (titre.length()
                < MIN_TITLE_LENGTH) {

            throw new IllegalArgumentException(
                    "Le titre doit contenir "
                            + "au moins 3 caractères."
            );
        }

        if (titre.length()
                > MAX_TITLE_LENGTH) {

            throw new IllegalArgumentException(
                    "Le titre ne doit pas dépasser "
                            + "255 caractères."
            );
        }

        if (description.length()
                > MAX_DESCRIPTION_LENGTH) {

            throw new IllegalArgumentException(
                    "La description ne doit pas dépasser "
                            + "2000 caractères."
            );
        }

        if (localisation.length()
                > MAX_LOCATION_LENGTH) {

            throw new IllegalArgumentException(
                    "La localisation ne doit pas dépasser "
                            + "255 caractères."
            );
        }

        objet.setTitre(titre);
        objet.setDescription(description);
        objet.setType(type);
        objet.setLocalisation(localisation);
    }

    private String normaliser(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}