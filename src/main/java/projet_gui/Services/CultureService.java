package projet_gui.Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import projet_gui.Utils.DataSource;
import projet_gui.Entities.Culture;

public class CultureService {
    private Connection connection;
    private static CultureService instance;
    
    // Singleton pattern
    private CultureService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static CultureService getInstance() {
        if (instance == null) {
            instance = new CultureService();
        }
        return instance;
    }

    // getAllByUserId
    public List<Culture> getAllByUserId(int userId) throws SQLException {
        // use a JOIN to get cultures associated with the user's parcelles
        String query = "SELECT c.* FROM Culture c " +
                       "JOIN Parcelle p ON c.parcelleId = p.id " +
                       "WHERE p.proprietaireId = ?";
        List<Culture> cultures = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Culture culture = new Culture();
                culture.setId(rs.getInt("id"));
                culture.setNom(rs.getString("nom"));
                culture.setBesoinEau(rs.getDouble("besoinEau"));
                culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
                culture.setStatut(Culture.Statut.valueOf(rs.getString("statut")));
                culture.setImagePath(rs.getString("imagePath"));
                culture.setParcelle(null); // Parcelle object can be set later if needed
                cultures.add(culture);
            }
        }
        return cultures;
    }

    // Get a culture by id
    public Culture getCultureById(int id) throws SQLException {
        String query = "SELECT * FROM Culture WHERE id = ?";
        Culture culture = null;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                culture = new Culture();
                culture.setId(rs.getInt("id"));
                culture.setNom(rs.getString("nom"));
                culture.setBesoinEau(rs.getDouble("besoinEau"));
                culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
                culture.setStatut(Culture.Statut.valueOf(rs.getString("statut")));
                culture.setImagePath(rs.getString("imagePath"));
                culture.setParcelle(null); // Parcelle object can be set later if needed
            }
        }

        return culture;
    }

    // Gets all cultures by parcelle id
    public List<Culture> getCulturesByParcelleId(int parcelleId) throws SQLException {
        String query = "SELECT * FROM Culture WHERE parcelleId = ?";
        List<Culture> cultures = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, parcelleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Culture culture = new Culture();
                culture.setId(rs.getInt("id"));
                culture.setNom(rs.getString("nom"));
                culture.setBesoinEau(rs.getDouble("besoinEau"));
                culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
                culture.setStatut(Culture.Statut.valueOf(rs.getString("statut")));
                culture.setImagePath(rs.getString("imagePath"));
                culture.setParcelle(null); // Parcelle object can be set later if needed
                cultures.add(culture);
            }
        }

        return cultures;
    }

    // Deletes a culture by id
    // make it return a boolean to indicate success or failure
    public boolean deleteCultureById(int cultureId) throws SQLException {
        String query = "DELETE FROM Culture WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, cultureId);
            ps.executeUpdate();

            return ps.getUpdateCount() > 0; // Return true if a row was deleted
        }
    }
}
