package projet_gui.Services;

import projet_gui.Entities.Culture;
import projet_gui.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CultureService {
    private static CultureService instance;

    // Singleton pattern
    private CultureService() {
    }

    public static synchronized CultureService getInstance() {
        if (instance == null) {
            instance = new CultureService();
        }
        return instance;
    }

    // Create (Insert) a new culture
    public void saveCulture(Culture culture) throws SQLException {
        String query = "INSERT INTO Culture (nom, besoinEau, besoinNutriments, imagePath, description) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, culture.getNom());
            stmt.setDouble(2, culture.getBesoinEau());
            stmt.setDouble(3, culture.getBesoinNutriments());
            stmt.setString(4, culture.getImagePath());
            stmt.setString(5, culture.getDescription());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                culture.setId(rs.getInt(1));
            }
        }
    }

    // Read (Retrieve all cultures)
    public List<Culture> getAllCultures() throws SQLException {
        List<Culture> cultures = new ArrayList<>();
        String query = "SELECT * FROM Culture";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Culture culture = new Culture();
                culture.setId(rs.getInt("id"));
                culture.setNom(rs.getString("nom"));
                culture.setBesoinEau(rs.getDouble("besoinEau"));
                culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
                culture.setImagePath(rs.getString("imagePath"));
                culture.setDescription(rs.getString("description"));
                cultures.add(culture);
            }
        }
        return cultures;
    }

    // Read (Retrieve a specific culture by ID)
    public Culture getCultureById(int id) throws SQLException {
        String query = "SELECT * FROM Culture WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Culture culture = new Culture();
                    culture.setId(rs.getInt("id"));
                    culture.setNom(rs.getString("nom"));
                    culture.setBesoinEau(rs.getDouble("besoinEau"));
                    culture.setBesoinNutriments(rs.getDouble("besoinNutriments"));
                    culture.setImagePath(rs.getString("imagePath"));
                    culture.setDescription(rs.getString("description"));
                    return culture;
                }
            }
        }
        return null;
    }

    // Update an existing culture
    public Culture updateCulture(Culture culture) throws SQLException {
        String query = "UPDATE Culture SET nom = ?, besoinEau = ?, besoinNutriments = ?, imagePath = ?, description = ? " +
                "WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, culture.getNom());
            stmt.setDouble(2, culture.getBesoinEau());
            stmt.setDouble(3, culture.getBesoinNutriments());
            stmt.setString(4, culture.getImagePath());
            stmt.setString(5, culture.getDescription());
            stmt.setInt(6, culture.getId());
            stmt.executeUpdate();
        }
        return culture;
    }

    // Delete a culture by ID
    public void deleteCulture(int id) throws SQLException {
        String query = "DELETE FROM Culture WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}