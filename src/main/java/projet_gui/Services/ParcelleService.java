package projet_gui.Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Utilisateur;
import projet_gui.Utils.DataSource;

public class ParcelleService {
    private Connection connection;
    private static ParcelleService instance;
    
    // Singleton pattern
    private ParcelleService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static ParcelleService getInstance() {
        if (instance == null) {
            instance = new ParcelleService();
        }
        return instance;
    }
    
    /**
     * Create a new parcelle in the database
     * @param parcelle The parcelle to create
     * @return The created parcelle with its ID
     */
    public Parcelle create(Parcelle parcelle) throws SQLException {
        String query = "INSERT INTO Parcelle (nom, longuerMetre, largeurMetre, localisationCity, proprietaireId, imagePath, dateCreation) "
                + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, parcelle.getNom());
            ps.setDouble(2, parcelle.getLongueur());
            ps.setDouble(3, parcelle.getLargeur());
            ps.setString(4, parcelle.getLocalisationCity());
            ps.setInt(5, parcelle.getProprietaire().getId());
            ps.setString(6, parcelle.getImagePath());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating parcelle failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    parcelle.setId(generatedKeys.getInt(1));
                    // Set the creation date
                    parcelle.setDateCreation(new Timestamp(System.currentTimeMillis()));
                    return parcelle;
                } else {
                    throw new SQLException("Creating parcelle failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get a parcelle by its ID
     * @param id The ID of the parcelle
     * @return The parcelle or null if not found
     */
    public Parcelle getById(int id) throws SQLException {
        String query = "SELECT p.*, u.id as userId, u.nom as userNom, u.prenom, u.email, u.role "
                + "FROM Parcelle p "
                + "JOIN Utilisateur u ON p.proprietaireId = u.id "
                + "WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToParcelle(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Get all parcelles for a specific user
     * @param userId The ID of the user
     * @return List of parcelles
     */
    public List<Parcelle> getAllByUserId(int userId) throws SQLException {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT p.*, u.id as userId, u.nom as userNom, u.prenom, u.email, u.role "
                + "FROM Parcelle p "
                + "JOIN Utilisateur u ON p.proprietaireId = u.id "
                + "WHERE p.proprietaireId = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parcelles.add(mapResultSetToParcelle(rs));
                }
            }
        }
        
        return parcelles;
    }
    
    /**
     * Get all parcelles
     * @return List of all parcelles
     */
    public List<Parcelle> getAll() throws SQLException {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT p.*, u.id as userId, u.nom as userNom, u.prenom, u.email, u.role "
                + "FROM Parcelle p "
                + "JOIN Utilisateur u ON p.proprietaireId = u.id";
        
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    parcelles.add(mapResultSetToParcelle(rs));
                }
            }
        }
        
        return parcelles;
    }
    
    /**
     * Update a parcelle
     * @param parcelle The parcelle to update
     * @return The updated parcelle
     */
    public Parcelle update(Parcelle parcelle) throws SQLException {
        String query = "UPDATE Parcelle SET nom = ?, longuerMetre = ?, largeurMetre = ?, "
                + "localisationCity = ?, proprietaireId = ?, imagePath = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, parcelle.getNom());
            ps.setDouble(2, parcelle.getLongueur());
            ps.setDouble(3, parcelle.getLargeur());
            ps.setString(4, parcelle.getLocalisationCity());
            ps.setInt(5, parcelle.getProprietaire().getId());
            ps.setString(6, parcelle.getImagePath());
            ps.setInt(7, parcelle.getId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating parcelle failed, no rows affected.");
            }
            
            return parcelle;
        }
    }
    
    /**
     * Delete a parcelle
     * @param id The ID of the parcelle to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM Parcelle WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Parcelle object
     * @param rs The ResultSet
     * @return The mapped Parcelle
     */
    private Parcelle mapResultSetToParcelle(ResultSet rs) throws SQLException {
        Parcelle parcelle = new Parcelle();
        parcelle.setId(rs.getInt("id"));
        parcelle.setNom(rs.getString("nom"));
        parcelle.setLongueur(rs.getDouble("longuerMetre"));
        parcelle.setLargeur(rs.getDouble("largeurMetre"));
        parcelle.setLocalisationCity(rs.getString("localisationCity"));
        parcelle.setImagePath(rs.getString("imagePath"));
        parcelle.setDateCreation(rs.getTimestamp("dateCreation"));
        
        // Create and set the proprietaire
        Utilisateur proprietaire = new Utilisateur();
        proprietaire.setId(rs.getInt("userId"));
        proprietaire.setNom(rs.getString("userNom"));
        proprietaire.setPrenom(rs.getString("prenom"));
        proprietaire.setEmail(rs.getString("email"));
        // proprietaire.setRole(rs.getString("role"));
        
        parcelle.setProprietaire(proprietaire);
        
        return parcelle;
    }
}
