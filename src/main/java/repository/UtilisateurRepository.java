package repository;

import java.util.List;

import modele.Utilisateur;

public interface UtilisateurRepository {

    void add(Utilisateur utilisateur);

    List<Utilisateur> selectAll();

    Utilisateur getById(int id);

    void delete(int id);

    Utilisateur authentifier(String email, String motdepass);

    boolean emailExiste(String email);

    boolean nomExiste(String nom);

    void promouvoirEnAdmin(int id);

    void retrograderEnUser(int id);

    int countAll();

    int countAdmins();
}
