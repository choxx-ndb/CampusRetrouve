package repository;

import java.util.List;

import modele.MessageReclamation;
import modele.Reclamation;

public interface ReclamationRepository {

    void add(Reclamation reclamation);

    List<Reclamation> selectAll();

    Reclamation getById(int id);

    void update(Reclamation reclamation);

    List<Reclamation> findByUtilisateur(int utilisateurId);

    List<Reclamation> findRecuesPourProprietaire(int proprietaireId);

    List<Reclamation> findByStatus(String status);

    void ajouterMessage(MessageReclamation message);

    List<MessageReclamation> getMessagesByReclamation(int reclamationId);
}
