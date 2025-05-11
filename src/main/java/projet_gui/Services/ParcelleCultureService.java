package projet_gui.Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import projet_gui.Entities.Culture;
import projet_gui.Entities.Maladie;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.ParcelleCulture;
import projet_gui.Utils.DataSource;

public class ParcelleCultureService {
    private Connection connection;
    private static ParcelleCultureService instance;
    
    // Singleton pattern
    private ParcelleCultureService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static ParcelleCultureService getInstance() {
        if (instance == null) {
            instance = new ParcelleCultureService();
        }
        return instance;
    }
    
    /**
     * Create a new parcelle-culture relationship in the database
     * @param parcelleCulture The parcelle-culture relationship to create
     * @return The created parcelle-culture with its ID
     */
    public ParcelleCulture create(ParcelleCulture parcelleCulture) throws SQLException {
        String query = "INSERT INTO ParcelleCulture (parcelleId, cultureId, maladieId, statut, dateAjout) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, parcelleCulture.getParcelle().getId());
            ps.setInt(2, parcelleCulture.getCulture().getId());
            
            if (parcelleCulture.getMaladie() != null) {
                ps.setInt(3, parcelleCulture.getMaladie().getId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            
            ps.setString(4, parcelleCulture.getStatut());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating parcelle-culture failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    parcelleCulture.setId(generatedKeys.getInt(1));
                    // Set the creation date
                    parcelleCulture.setDateAjout(new Timestamp(System.currentTimeMillis()));
                    return parcelleCulture;
                } else {
                    throw new SQLException("Creating parcelle-culture failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get a parcelle-culture by its ID
     * @param id The ID of the parcelle-culture
     * @return The parcelle-culture or null if not found
     */
    public ParcelleCulture getById(int id) throws SQLException {
        String query = "SELECT pc.*, p.id as parcelleId, p.nom as parcelleNom, p.longuerMetre, p.largeurMetre, "
                + "p.localisationCity, p.proprietaireId, p.imagePath as parcelleImage, p.dateCreation, "
                + "c.id as cultureId, c.nom as cultureNom, c.besoinEau, c.besoinNutriments, "
                + "c.imagePath as cultureImage, c.description, "
                + "m.id as maladieId, m.nom as maladieNom, m.description as maladieDesc, "
                + "m.traitement, m.symptomes, m.imagePath as maladieImage "
                + "FROM ParcelleCulture pc "
                + "JOIN Parcelle p ON pc.parcelleId = p.id "
                + "JOIN Culture c ON pc.cultureId = c.id "
                + "LEFT JOIN Maladie m ON pc.maladieId = m.id "
                + "WHERE pc.id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToParcelleCulture(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Get all active crops for a specific parcelle
     * @param parcelleId The ID of the parcelle
     * @return List of parcelle-cultures
     */
    public List<ParcelleCulture> getActiveByParcelleId(int parcelleId) throws SQLException {
        List<ParcelleCulture> parcelleCultures = new ArrayList<>();
        String query = "SELECT pc.*, p.id as parcelleId, p.nom as parcelleNom, p.longuerMetre, p.largeurMetre, "
                + "p.localisationCity, p.proprietaireId, p.imagePath as parcelleImage, p.dateCreation, "
                + "c.id as cultureId, c.nom as cultureNom, c.besoinEau, c.besoinNutriments, "
                + "c.imagePath as cultureImage, c.description, "
                + "m.id as maladieId, m.nom as maladieNom, m.description as maladieDesc, "
                + "m.traitement, m.symptomes, m.imagePath as maladieImage "
                + "FROM ParcelleCulture pc "
                + "JOIN Parcelle p ON pc.parcelleId = p.id "
                + "JOIN Culture c ON pc.cultureId = c.id "
                + "LEFT JOIN Maladie m ON pc.maladieId = m.id "
                + "WHERE pc.parcelleId = ? AND pc.statut IN (?, ?)"; // Only HEALTHY and SICK are active
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleId);
            ps.setString(2, ParcelleCulture.STATUT_HEALTHY);
            ps.setString(3, ParcelleCulture.STATUT_SICK);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parcelleCultures.add(mapResultSetToParcelleCulture(rs));
                }
            }
        }
        
        return parcelleCultures;
    }
    
    /**
     * Get all parcelle-cultures
     * @return List of all parcelle-cultures
     */
    public List<ParcelleCulture> getAll() throws SQLException {
        List<ParcelleCulture> parcelleCultures = new ArrayList<>();
        String query = "SELECT pc.*, p.id as parcelleId, p.nom as parcelleNom, p.longuerMetre, p.largeurMetre, "
                + "p.localisationCity, p.proprietaireId, p.imagePath as parcelleImage, p.dateCreation, "
                + "c.id as cultureId, c.nom as cultureNom, c.besoinEau, c.besoinNutriments, "
                + "c.imagePath as cultureImage, c.description, "
                + "m.id as maladieId, m.nom as maladieNom, m.description as maladieDesc, "
                + "m.traitement, m.symptomes, m.imagePath as maladieImage "
                + "FROM ParcelleCulture pc "
                + "JOIN Parcelle p ON pc.parcelleId = p.id "
                + "JOIN Culture c ON pc.cultureId = c.id "
                + "LEFT JOIN Maladie m ON pc.maladieId = m.id";
        
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    parcelleCultures.add(mapResultSetToParcelleCulture(rs));
                }
            }
        }
        
        return parcelleCultures;
    }
    
    /**
     * Update a parcelle-culture
     * @param parcelleCulture The parcelle-culture to update
     * @return The updated parcelle-culture
     */
    public ParcelleCulture update(ParcelleCulture parcelleCulture) throws SQLException {
        String query = "UPDATE ParcelleCulture SET parcelleId = ?, cultureId = ?, "
                + "maladieId = ?, statut = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleCulture.getParcelle().getId());
            ps.setInt(2, parcelleCulture.getCulture().getId());
            
            if (parcelleCulture.getMaladie() != null) {
                ps.setInt(3, parcelleCulture.getMaladie().getId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            
            ps.setString(4, parcelleCulture.getStatut());
            ps.setInt(5, parcelleCulture.getId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating parcelle-culture failed, no rows affected.");
            }
            
            return parcelleCulture;
        }
    }
    
    /**
     * Delete a parcelle-culture
     * @param id The ID of the parcelle-culture to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM ParcelleCulture WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Get active crops count for a parcelle
     * @param parcelleId The ID of the parcelle
     * @return The number of active crops
     */
    public int getActiveCropsCount(int parcelleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM ParcelleCulture "
                + "WHERE parcelleId = ? AND statut IN (?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleId);
            ps.setString(2, ParcelleCulture.STATUT_HEALTHY);
            ps.setString(3, ParcelleCulture.STATUT_SICK);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    /**
     * Get active crops names for a parcelle (comma-separated)
     * @param parcelleId The ID of the parcelle
     * @return Comma-separated list of crop names
     */
    public String getActiveCropsNames(int parcelleId) throws SQLException {
        List<String> cropNames = new ArrayList<>();
        String query = "SELECT c.nom FROM ParcelleCulture pc "
                + "JOIN Culture c ON pc.cultureId = c.id "
                + "WHERE pc.parcelleId = ? AND pc.statut IN (?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleId);
            ps.setString(2, ParcelleCulture.STATUT_HEALTHY);
            ps.setString(3, ParcelleCulture.STATUT_SICK);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cropNames.add(rs.getString("nom"));
                }
            }
        }
        
        return String.join(", ", cropNames);
    }
    
    /**
     * Map a ResultSet to a ParcelleCulture object
     * @param rs The ResultSet
     * @return The mapped ParcelleCulture
     */
    private ParcelleCulture mapResultSetToParcelleCulture(ResultSet rs) throws SQLException {
        ParcelleCulture parcelleCulture = new ParcelleCulture();
        parcelleCulture.setId(rs.getInt("id"));
        parcelleCulture.setStatut(rs.getString("statut"));
        parcelleCulture.setDateAjout(rs.getTimestamp("dateAjout"));
        
        // Create and set the parcelle
        Parcelle parcelle = new Parcelle();
        parcelle.setId(rs.getInt("parcelleId"));
        parcelle.setNom(rs.getString("parcelleNom"));
        parcelle.setLongueur(rs.getDouble("longuerMetre"));
        parcelle.setLargeur(rs.getDouble("largeurMetre"));
        parcelle.setLocalisationCity(rs.getString("localisationCity"));
        parcelle.setImagePath(rs.getString("parcelleImage"));
        parcelle.setDateCreation(rs.getTimestamp("dateCreation"));
        
        parcelleCulture.setParcelle(parcelle);
        
        // Create and set the culture
        Culture culture = new Culture();
        culture.setId(rs.getInt("cultureId"));
        culture.setNom(rs.getString("cultureNom"));
        culture.setBesoinEau(rs.getDouble("besoinEau"));
        culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
        culture.setImagePath(rs.getString("cultureImage"));
        culture.setDescription(rs.getString("description"));
        
        parcelleCulture.setCulture(culture);
        
        // Create and set the maladie if it exists
        int maladieId = rs.getInt("maladieId");
        if (!rs.wasNull()) {
            Maladie maladie = new Maladie();
            maladie.setId(maladieId);
            maladie.setNom(rs.getString("maladieNom"));
            maladie.setDescription(rs.getString("maladieDesc"));
            maladie.setTraitement(rs.getString("traitement"));
            maladie.setSymptomes(rs.getString("symptomes"));
            maladie.setImagePath(rs.getString("maladieImage"));
            
            parcelleCulture.setMaladie(maladie);
        }
        
        return parcelleCulture;
    }
}