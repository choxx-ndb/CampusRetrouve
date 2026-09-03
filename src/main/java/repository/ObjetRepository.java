package repository;

import java.util.List;

import modele.Objet;

public interface ObjetRepository {

    void add(Objet objet);

    List<Objet> selectAll();

    Objet getById(int id);

    void delete(int id);

    List<Objet> findByType(String type);

    List<Objet> findByUserId(int proprietaireId);

    void updateContent(Objet objet);

    void updateStatus(int objetId, String status);

    int countAll();
}