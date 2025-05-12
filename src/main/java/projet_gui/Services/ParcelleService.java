package projet_gui.Services;

import projet_gui.Entities.Localisation;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Utilisateur;
import projet_gui.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelleService {
    private static ParcelleService instance;
    private final UtilisateurService utilisateurService = UtilisateurService.getInstance();

    private ParcelleService() {}

    public static synchronized ParcelleService getInstance() {
        if (instance == null) {
            instance = new ParcelleService();
        }
        return instance;
    }

    // Create (Insert) a new Parcelle
    public void saveParcelle(Parcelle parcelle) throws SQLException {
        if (parcelle.getLocalisation() == null || parcelle.getProprietaire() == null) {
            throw new IllegalArgumentException("Localisation and Proprietaire cannot be null");
        }
        String query = "INSERT INTO Parcelle (nom, superficie, area, localisation_id, proprietaire_id, imagePath, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, parcelle.getNom());
            stmt.setDouble(2, parcelle.getSuperficie());
            stmt.setDouble(3, parcelle.getArea());
            stmt.setInt(4, parcelle.getLocalisation().getId());
            stmt.setInt(5, parcelle.getProprietaire().getId());
            stmt.setString(6, parcelle.getImagePath());
            stmt.setString(7, parcelle.getStatut());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                parcelle.setId(rs.getInt(1));
                parcelle.setDateCreation(rs.getTimestamp("dateCreation"));
                parcelle.setDerniereMiseAJour(rs.getTimestamp("derniereMiseAJour"));
            }
        }
    }

    // Read (Retrieve all Parcelles)
    public List<Parcelle> getAllParcelles() throws SQLException {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT * FROM Parcelle";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Parcelle p = new Parcelle();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setSuperficie(rs.getDouble("superficie"));
                p.setArea(rs.getDouble("area"));
              //  p.setLocalisation(getLocalisationById(rs.getInt("localisation_id")));
             //   p.setProprietaire(utilisateurService.getUserById(rs.getInt("proprietaire_id")).orElse(null));
                p.setImagePath(rs.getString("imagePath"));
                p.setStatut(rs.getString("statut"));
                p.setDateCreation(rs.getTimestamp("dateCreation"));
                p.setDerniereMiseAJour(rs.getTimestamp("derniereMiseAJour"));
                parcelles.add(p);
            }
        }
        return parcelles;
    }

    // Read (Retrieve a specific Parcelle by ID)
    public Parcelle getParcelleById(int id) throws SQLException {
        String query = "SELECT * FROM Parcelle WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Parcelle p = new Parcelle();
                    p.setId(rs.getInt("id"));
                    p.setNom(rs.getString("nom"));
                    p.setSuperficie(rs.getDouble("superficie"));
                    p.setArea(rs.getDouble("area"));
                  //  p.setLocalisation(getLocalisationById(rs.getInt("localisation_id")));
                   // p.setProprietaire(utilisateurService.getUserById(rs.getInt("proprietaire_id")).orElse(null));
                    p.setImagePath(rs.getString("imagePath"));
                    p.setStatut(rs.getString("statut"));
                    p.setDateCreation(rs.getTimestamp("dateCreation"));
                    p.setDerniereMiseAJour(rs.getTimestamp("derniereMiseAJour"));
                    return p;
                }
            }
        }
        return null;
    }

    // Update an existing Parcelle
    public void updateParcelle(Parcelle parcelle) throws SQLException {
        if (parcelle.getLocalisation() == null /*|| parcelle.getProprietaire() == null*/) {
            throw new IllegalArgumentException("Localisation and Proprietaire cannot be null");
        }
        String query = "UPDATE Parcelle SET nom = ?, superficie = ?, area = ?, localisation_id = ?, proprietaire_id = ?, imagePath = ?, statut = ? WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, parcelle.getNom());
            stmt.setDouble(2, parcelle.getSuperficie());
            stmt.setDouble(3, parcelle.getArea());
            stmt.setInt(4, parcelle.getLocalisation().getId());
            stmt.setInt(5, parcelle.getProprietaire().getId());
            stmt.setString(6, parcelle.getImagePath());
            stmt.setString(7, parcelle.getStatut());
            stmt.setInt(8, parcelle.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a Parcelle by ID
    public void deleteParcelle(int id) throws SQLException {
        String query = "DELETE FROM Parcelle WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /*// Helper method to fetch Localisation by ID (placeholder)
    private Localisation getLocalisationById(int localisationId) throws SQLException {
        // Placeholder: Replace with actual LocalisationService if available
        Localisation localisation = new Localisation();
        localisation.setId(localisationId);
       // localisation.setDescription("Location " + localisationId);
        return localisation;
    }*/
}