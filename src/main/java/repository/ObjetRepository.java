package repository;

import java.util.List;

import modele.Objet;

public interface ObjetRepository {

    void add(Objet objet);

    List<Objet> selectAll();

    Objet getById(int id);

    void delete(int id);

    List<Objet> findByType(String type);

    void updateStatus(int objetId, String status);
}
