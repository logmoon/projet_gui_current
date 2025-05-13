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
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Tache;
import projet_gui.Entities.Utilisateur;
import projet_gui.Utils.DataSource;

public class TacheService {
    private Connection connection;
    private static TacheService instance;
    
    // Singleton pattern
    private TacheService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static TacheService getInstance() {
        if (instance == null) {
            instance = new TacheService();
        }
        return instance;
    }

    // getNotCompletedAllByUserId
    public List<Tache> getNotCompletedAllByUserId(int userId) throws SQLException {
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE t.statut != ? AND p.proprietaireId = ?";
        
        List<Tache> taches = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, Tache.STATUT_DONE);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Create a new task in the database
     * @param tache The task to create
     * @return The created task with its ID
     */
    public Tache create(Tache tache) throws SQLException {
        String query = "INSERT INTO Tache (parcelleId, description, statut, priorite, dateCreation, dateEcheance, cultureId) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tache.getParcelle().getId());
            ps.setString(2, tache.getDescription());
            ps.setString(3, tache.getStatut() != null ? tache.getStatut() : Tache.STATUT_PENDING);
            ps.setString(4, tache.getPriorite() != null ? tache.getPriorite() : Tache.PRIORITE_MEDIUM);
            ps.setTimestamp(5, tache.getDateEcheance());
            
            // Handle optional culture
            if (tache.getCulture() != null) {
                ps.setInt(6, tache.getCulture().getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tache.setId(generatedKeys.getInt(1));
                    // Set the creation date
                    tache.setDateCreation(new Timestamp(System.currentTimeMillis()));
                    return tache;
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get a task by its ID
     * @param id The ID of the task
     * @return The task or null if not found
     */
    public Tache getById(int id) throws SQLException {
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE t.id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTache(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Get all tasks
     * @return List of all tasks
     */
    public List<Tache> getAll() throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id";
        
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Get all tasks by parcelle ID
     * @param parcelleId The ID of the parcelle
     * @return List of tasks for the parcelle
     */
    public List<Tache> getAllByParcelleId(int parcelleId) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE t.parcelleId = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Get all tasks by status
     * @param status The status to filter by
     * @return List of tasks with the given status
     */
    public List<Tache> getAllByStatus(String status) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE t.statut = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, status);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Get all tasks by priority
     * @param priority The priority to filter by
     * @return List of tasks with the given priority
     */
    public List<Tache> getAllByPriority(String priority) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        String query = "SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE t.priorite = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, priority);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Get filtered tasks based on multiple criteria
     * @param status Optional status filter
     * @param priority Optional priority filter
     * @param parcelleId Optional parcelle ID filter
     * @return List of tasks matching the filters
     */
    public List<Tache> getFiltered(String status, String priority, Integer parcelleId) throws SQLException {
        List<Tache> taches = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT t.*, p.id as parcelleId, p.nom as parcelleNom, "
                + "c.id as cultureId, c.nom as cultureNom "
                + "FROM Tache t "
                + "JOIN Parcelle p ON t.parcelleId = p.id "
                + "LEFT JOIN Culture c ON t.cultureId = c.id "
                + "WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            queryBuilder.append(" AND t.statut = ?");
            params.add(status);
        }
        
        if (priority != null && !priority.isEmpty()) {
            queryBuilder.append(" AND t.priorite = ?");
            params.add(priority);
        }
        
        if (parcelleId != null) {
            queryBuilder.append(" AND t.parcelleId = ?");
            params.add(parcelleId);
        }
        
        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taches.add(mapResultSetToTache(rs));
                }
            }
        }
        
        return taches;
    }
    
    /**
     * Update a task
     * @param tache The task to update
     * @return The updated task
     */
    public Tache update(Tache tache) throws SQLException {
        String query = "UPDATE Tache SET parcelleId = ?, description = ?, statut = ?, "
                + "priorite = ?, dateEcheance = ?, cultureId = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, tache.getParcelle().getId());
            ps.setString(2, tache.getDescription());
            ps.setString(3, tache.getStatut());
            ps.setString(4, tache.getPriorite());
            ps.setTimestamp(5, tache.getDateEcheance());
            
            // Handle optional culture
            if (tache.getCulture() != null) {
                ps.setInt(6, tache.getCulture().getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            
            ps.setInt(7, tache.getId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating task failed, no rows affected.");
            }
            
            return tache;
        }
    }
    
    /**
     * Update task status
     * @param taskId The ID of the task
     * @param newStatus The new status
     * @return true if updated, false otherwise
     */
    public boolean updateStatus(int taskId, String newStatus) throws SQLException {
        String query = "UPDATE Tache SET statut = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, taskId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a task
     * @param id The ID of the task to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM Tache WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Tache object
     * @param rs The ResultSet
     * @return The mapped Tache
     */
    private Tache mapResultSetToTache(ResultSet rs) throws SQLException {
        Tache tache = new Tache();
        tache.setId(rs.getInt("id"));
        tache.setDescription(rs.getString("description"));
        tache.setStatut(rs.getString("statut"));
        tache.setPriorite(rs.getString("priorite"));
        tache.setDateCreation(rs.getTimestamp("dateCreation"));
        tache.setDateEcheance(rs.getTimestamp("dateEcheance"));
        
        // Create and set the parcelle
        Parcelle parcelle = new Parcelle();
        parcelle.setId(rs.getInt("parcelleId"));
        parcelle.setNom(rs.getString("parcelleNom"));
        tache.setParcelle(parcelle);
        
        // Create and set the culture if exists
        int cultureId = rs.getInt("cultureId");
        if (!rs.wasNull()) {
            Culture culture = new Culture();
            culture.setId(cultureId);
            culture.setNom(rs.getString("cultureNom"));
            tache.setCulture(culture);
        }
        
        return tache;
    }
}