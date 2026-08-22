package service;

import java.util.List;
import java.util.Objects;

import dao.ObjetDAO;
import modele.Objet;
import repository.ObjetRepository;

public class ObjetService {

    private final ObjetRepository objetRepository;

    public ObjetService() {
        this(new ObjetDAO());
    }

    public ObjetService(ObjetRepository objetRepository) {
        this.objetRepository = Objects.requireNonNull(
            objetRepository,
            "objetRepository ne peut pas être null"
        );
    }

    public void publierAnnonce(Objet objet) {
        validerObjet(objet);

        objet.setStatus("disponible");

        objetRepository.add(objet);
    }

    public List<Objet> recupererTous() {
        return objetRepository.selectAll();
    }

    public List<Objet> recupererParType(String type) {
        if (!"perdue".equals(type) && !"trouve".equals(type)) {
            return recupererTous();
        }

        return objetRepository.findByType(type);
    }

    public Objet recupererParId(int id) {
        return objetRepository.getById(id);
    }

    public void supprimer(int id) {
        objetRepository.delete(id);
    }

    public void changerStatut(int id, String status) {
        if (!"disponible".equals(status)
                && !"reclame".equals(status)
                && !"restitue".equals(status)) {
            throw new IllegalArgumentException("Statut objet invalide.");
        }

        objetRepository.updateStatus(id, status);
    }

    private void validerObjet(Objet objet) {
        if (objet == null
                || isBlank(objet.getTitre())
                || isBlank(objet.getType())
                || objet.getProprietaireId() <= 0) {
            throw new IllegalArgumentException(
                "Titre, type et propriétaire sont obligatoires."
            );
        }

        if (!"perdue".equals(objet.getType())
                && !"trouve".equals(objet.getType())) {
            throw new IllegalArgumentException(
                "Le type doit être 'perdue' ou 'trouve'."
            );
        }

        if (objet.getTitre().trim().length() < 3) {
            throw new IllegalArgumentException(
                "Le titre doit contenir au moins 3 caractères."
            );
        }

        objet.setTitre(objet.getTitre().trim());

        objet.setDescription(
            objet.getDescription() == null
                ? ""
                : objet.getDescription().trim()
        );

        objet.setLocalisation(
            objet.getLocalisation() == null
                ? ""
                : objet.getLocalisation().trim()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}