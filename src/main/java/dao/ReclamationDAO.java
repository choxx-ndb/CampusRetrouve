package dao;

import dao.util.DBConnection;
import modele.MessageReclamation;
import modele.Reclamation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO implements CommonDAO<Reclamation> {
    private static final String BASE_SELECT = "SELECT r.*, o.titre AS objet_titre, o.proprietaire_id, demandeur.nom AS demandeur_nom, proprietaire.nom AS proprietaire_nom " +
            "FROM reclamations r " +
            "JOIN objets o ON o.id = r.objet_id " +
            "JOIN utilisateurs demandeur ON demandeur.id = r.utilisateur_id " +
            "JOIN utilisateurs proprietaire ON proprietaire.id = o.proprietaire_id ";

    @Override
    public void add(Reclamation reclamation) {
        String sql = "INSERT INTO reclamations (objet_id, utilisateur_id, message, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reclamation.getObjetId());
            ps.setInt(2, reclamation.getUtilisateurId());
            ps.setString(3, reclamation.getMessage());
            ps.setString(4, reclamation.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    reclamation.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout rÃ©clamation", e);
        }
    }

    @Override
    public List<Reclamation> selectAll() {
        return queryList(BASE_SELECT + "ORDER BY r.created_at DESC");
    }

    @Override
    public Reclamation getById(int id) {
        List<Reclamation> result = queryList(BASE_SELECT + "WHERE r.id = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void update(Reclamation reclamation) {
        String sql = "UPDATE reclamations SET objet_id = ?, utilisateur_id = ?, message = ?, status = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reclamation.getObjetId());
            ps.setInt(2, reclamation.getUtilisateurId());
            ps.setString(3, reclamation.getMessage());
            ps.setString(4, reclamation.getStatus());
            ps.setInt(5, reclamation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise Ã  jour rÃ©clamation", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reclamations WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression rÃ©clamation", e);
        }
    }

    public List<Reclamation> findByUtilisateur(int utilisateurId) {
        return queryList(BASE_SELECT + "WHERE r.utilisateur_id = ? ORDER BY r.created_at DESC", utilisateurId);
    }

    public List<Reclamation> findRecuesPourProprietaire(int proprietaireId) {
        return queryList(BASE_SELECT + "WHERE o.proprietaire_id = ? ORDER BY r.created_at DESC", proprietaireId);
    }

    public List<Reclamation> findByStatus(String status) {
        return queryList(BASE_SELECT + "WHERE r.status = ? ORDER BY r.created_at DESC", status);
    }

    public void ajouterMessage(MessageReclamation message) {
        String sql = "INSERT INTO messages_reclamation (reclamation_id, expediteur_id, contenu) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, message.getReclamationId());
            ps.setInt(2, message.getExpediteurId());
            ps.setString(3, message.getContenu());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout message", e);
        }
    }

    public List<MessageReclamation> getMessagesByReclamation(int reclamationId) {
        List<MessageReclamation> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom AS expediteur_nom FROM messages_reclamation m JOIN utilisateurs u ON u.id = m.expediteur_id WHERE m.reclamation_id = ? ORDER BY m.date_envoi ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reclamationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MessageReclamation message = new MessageReclamation();
                    message.setId(rs.getInt("id"));
                    message.setReclamationId(rs.getInt("reclamation_id"));
                    message.setExpediteurId(rs.getInt("expediteur_id"));
                    message.setContenu(rs.getString("contenu"));
                    message.setDateEnvoi(rs.getTimestamp("date_envoi"));
                    message.setExpediteurNom(rs.getString("expediteur_nom"));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture messages", e);
        }
        return messages;
    }

    public int countEnAttente() {
        try (Connection connection = DBConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM reclamations WHERE status = 'en_attente'")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage rÃ©clamations", e);
        }
    }

    private List<Reclamation> queryList(String sql, Object... params) {
        List<Reclamation> reclamations = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture rÃ©clamations", e);
        }
        return reclamations;
    }

    private Reclamation map(ResultSet rs) throws SQLException {
        Reclamation reclamation = new Reclamation();
        reclamation.setId(rs.getInt("id"));
        reclamation.setObjetId(rs.getInt("objet_id"));
        reclamation.setUtilisateurId(rs.getInt("utilisateur_id"));
        reclamation.setMessage(rs.getString("message"));
        reclamation.setStatus(rs.getString("status"));
        reclamation.setCreatedAt(rs.getTimestamp("created_at"));
        reclamation.setObjetTitre(rs.getString("objet_titre"));
        reclamation.setDemandeurNom(rs.getString("demandeur_nom"));
        reclamation.setProprietaireNom(rs.getString("proprietaire_nom"));
        reclamation.setProprietaireId(rs.getInt("proprietaire_id"));
        return reclamation;
    }
}
