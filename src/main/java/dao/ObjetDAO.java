package dao;

import dao.util.DBConnection;
import modele.Objet;
import repository.ObjetRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ObjetDAO implements CommonDAO<Objet>, ObjetRepository {
    @Override
    public void add(Objet objet) {
        String sql = "INSERT INTO objets (titre, description, type, localisation, image_path, status, proprietaire_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            fillStatement(ps, objet);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout objet", e);
        }
    }

    @Override
    public List<Objet> selectAll() {
        return queryList("SELECT o.*, u.nom AS proprietaire_nom FROM objets o JOIN utilisateurs u ON u.id = o.proprietaire_id ORDER BY o.created_at DESC");
    }

    @Override
    public Objet getById(int id) {
        String sql = "SELECT o.*, u.nom AS proprietaire_nom FROM objets o JOIN utilisateurs u ON u.id = o.proprietaire_id WHERE o.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche objet", e);
        }
        return null;
    }

    @Override
    public void update(Objet objet) {
        String sql = "UPDATE objets SET titre = ?, description = ?, type = ?, localisation = ?, image_path = ?, status = ?, proprietaire_id = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            fillStatement(ps, objet);
            ps.setInt(8, objet.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise Ã  jour objet", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM objets WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression objet", e);
        }
    }
    @Override
    public List<Objet> findByType(String type) {
        return queryList("SELECT o.*, u.nom AS proprietaire_nom FROM objets o JOIN utilisateurs u ON u.id = o.proprietaire_id WHERE o.type = ? ORDER BY o.created_at DESC", type);
    }

    public List<Objet> findByUserId(int proprietaireId) {
        return queryList("SELECT o.*, u.nom AS proprietaire_nom FROM objets o JOIN utilisateurs u ON u.id = o.proprietaire_id WHERE o.proprietaire_id = ? ORDER BY o.created_at DESC", proprietaireId);
    }
    @Override
    public void updateStatus(int objetId, String status) {
        String sql = "UPDATE objets SET status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, objetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise Ã  jour du statut objet", e);
        }
    }

    public int countAll() {
        try (Connection connection = DBConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM objets")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage objets", e);
        }
    }

    private List<Objet> queryList(String sql, Object... params) {
        List<Objet> objets = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    objets.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture objets", e);
        }
        return objets;
    }

    private void fillStatement(PreparedStatement ps, Objet objet) throws SQLException {
        ps.setString(1, objet.getTitre());
        ps.setString(2, objet.getDescription());
        ps.setString(3, objet.getType());
        ps.setString(4, objet.getLocalisation());
        ps.setString(5, objet.getImagePath());
        ps.setString(6, objet.getStatus());
        ps.setInt(7, objet.getProprietaireId());
    }

    private Objet map(ResultSet rs) throws SQLException {
        Objet objet = new Objet();
        objet.setId(rs.getInt("id"));
        objet.setTitre(rs.getString("titre"));
        objet.setDescription(rs.getString("description"));
        objet.setType(rs.getString("type"));
        objet.setLocalisation(rs.getString("localisation"));
        objet.setImagePath(rs.getString("image_path"));
        objet.setStatus(rs.getString("status"));
        objet.setProprietaireId(rs.getInt("proprietaire_id"));
        objet.setCreatedAt(rs.getTimestamp("created_at"));
        objet.setProprietaireNom(rs.getString("proprietaire_nom"));
        return objet;
    }
}