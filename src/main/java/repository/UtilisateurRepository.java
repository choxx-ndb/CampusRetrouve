package repository;

import modele.Utilisateur;

public interface UtilisateurRepository {

    void add(Utilisateur utilisateur);

    Utilisateur getById(int id);

    Utilisateur authentifier(String email, String motdepass);

    boolean emailExiste(String email);

    boolean nomExiste(String nom);
}