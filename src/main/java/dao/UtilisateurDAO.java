package dao;

import dao.util.DBConnection;

import modele.Utilisateur;
import repository.UtilisateurRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO implements CommonDAO<Utilisateur>, UtilisateurRepository {
    @Override
    public void add(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs (nom, email, motdepass, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getEmail());
            ps.setString(3, utilisateur.getMotdepass());
            ps.setString(4, utilisateur.getRole());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout utilisateur", e);
        }
    }

    @Override
    public List<Utilisateur> selectAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                utilisateurs.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture utilisateurs", e);
        }
        return utilisateurs;
    }

    @Override
    public Utilisateur getById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche utilisateur", e);
        }
        return null;
    }

    @Override
    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET nom = ?, email = ?, motdepass = ?, role = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getEmail());
            ps.setString(3, utilisateur.getMotdepass());
            ps.setString(4, utilisateur.getRole());
            ps.setInt(5, utilisateur.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise Ã  jour utilisateur", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression utilisateur", e);
        }
    }

    public Utilisateur findByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par email", e);
        }
        return null;
    }
    @Override
    public Utilisateur authentifier(String email, String motdepass) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND motdepass = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, motdepass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'authentification", e);
        }
        return null;
    }
    @Override
    public boolean emailExiste(String email) {
        return exists("SELECT 1 FROM utilisateurs WHERE email = ?", email);
    }
    @Override
    public boolean nomExiste(String nom) {
        return exists("SELECT 1 FROM utilisateurs WHERE nom = ?", nom);
    }

    public void promouvoirEnAdmin(int id) {
        changerRole(id, "admin");
    }

    public void retrograderEnUser(int id) {
        changerRole(id, "user");
    }

    public int countAll() {
        return count("SELECT COUNT(*) FROM utilisateurs");
    }

    public int countAdmins() {
        return count("SELECT COUNT(*) FROM utilisateurs WHERE role = 'admin'");
    }

    private void changerRole(int id, String role) {
        String sql = "UPDATE utilisateurs SET role = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du changement de rÃ´le", e);
        }
    }

    private boolean exists(String sql, String value) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vÃ©rification d'existence", e);
        }
    }

    private int count(String sql) {
        try (Connection connection = DBConnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage", e);
        }
    }

    private Utilisateur map(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("email"),
                rs.getString("motdepass"),
                rs.getString("role"),
                rs.getTimestamp("created_at")
        );
    }
}
