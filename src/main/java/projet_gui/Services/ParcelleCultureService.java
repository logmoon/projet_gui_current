package projet_gui.Services;

import projet_gui.Entities.*;
import projet_gui.Utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelleCultureService {
    private static ParcelleCultureService instance;
    private final CultureService cultureService = CultureService.getInstance();
    private final ParcelleService parcelleService = ParcelleService.getInstance();
    private final MaladieService maladieService = MaladieService.getInstance();

    private ParcelleCultureService() {}

    public static synchronized ParcelleCultureService getInstance() {
        if (instance == null) {
            instance = new ParcelleCultureService();
        }
        return instance;
    }

    public void saveParcelleCulture(ParcelleCulture parcelleCulture) throws SQLException {
        validateParcelleCulture(parcelleCulture);

        String query = "INSERT INTO ParcelleCulture (parcelleId, cultureId, statut, maladieId) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParameters(stmt, parcelleCulture);
            stmt.executeUpdate();

            handleGeneratedKeys(conn, stmt, parcelleCulture);
        }
    }

    public List<ParcelleCulture> getAllParcelleCultures() throws SQLException {
        List<ParcelleCulture> parcelleCultures = new ArrayList<>();
        String query = "SELECT pc.*, p.nom AS parcelle_nom, c.nom AS culture_nom, " +
                "m.nom AS maladie_nom, m.description AS maladie_desc " +
                "FROM ParcelleCulture pc " +
                "JOIN Parcelle p ON pc.parcelleId = p.id " +
                "JOIN Culture c ON pc.cultureId = c.id " +
                "LEFT JOIN Maladie m ON pc.maladieId = m.id";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                parcelleCultures.add(mapResultSetToParcelleCulture(rs));
            }
        }
        return parcelleCultures;
    }

    public ParcelleCulture getParcelleCultureById(int id) throws SQLException {
        String query = "SELECT pc.*, p.nom AS parcelle_nom, c.nom AS culture_nom, " +
                "m.nom AS maladie_nom, m.description AS maladie_desc " +
                "FROM ParcelleCulture pc " +
                "JOIN Parcelle p ON pc.parcelleId = p.id " +
                "JOIN Culture c ON pc.cultureId = c.id " +
                "LEFT JOIN Maladie m ON pc.maladieId = m.id " +
                "WHERE pc.id = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToParcelleCulture(rs);
                }
            }
        }
        return null;
    }

    public void updateParcelleCulture(ParcelleCulture parcelleCulture) throws SQLException {
        validateParcelleCulture(parcelleCulture);

        String query = "UPDATE ParcelleCulture SET parcelleId = ?, cultureId = ?, " +
                "statut = ?, maladieId = ? WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            setStatementParameters(stmt, parcelleCulture);
            stmt.setInt(5, parcelleCulture.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteParcelleCulture(int id) throws SQLException {
        String query = "DELETE FROM ParcelleCulture WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Méthodes utilitaires privées
    private void validateParcelleCulture(ParcelleCulture pc) throws SQLException {
        if (pc == null) throw new IllegalArgumentException("ParcelleCulture cannot be null");
        if (pc.getParcelle() == null || parcelleService.getParcelleById(pc.getParcelle().getId()) == null) {
            throw new SQLException("Parcelle invalide ou introuvable");
        }
        if (pc.getCulture() == null || cultureService.getCultureById(pc.getCulture().getId()) == null) {
            throw new SQLException("Culture invalide ou introuvable");
        }
        if (pc.getMaladie() != null && maladieService.getMaladieById(pc.getMaladie().getId()) == null) {
            throw new SQLException("Maladie invalide ou introuvable");
        }
        if (pc.getStatut() == null || pc.getStatut().trim().isEmpty()) {
            throw new IllegalArgumentException("Statut cannot be null or empty");
        }
    }

    private void setStatementParameters(PreparedStatement stmt, ParcelleCulture pc) throws SQLException {
        stmt.setInt(1, pc.getParcelle().getId());
        stmt.setInt(2, pc.getCulture().getId());
        stmt.setString(3, pc.getStatut());
        if (pc.getMaladie() != null) {
            stmt.setInt(4, pc.getMaladie().getId());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
    }

    private void handleGeneratedKeys(Connection conn, PreparedStatement stmt,
                                     ParcelleCulture pc) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                pc.setId(rs.getInt(1));
                fetchDateAjout(conn, pc);
            }
        }
    }

    private void fetchDateAjout(Connection conn, ParcelleCulture pc) throws SQLException {
        String query = "SELECT dateAjout FROM ParcelleCulture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pc.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pc.setDateAjout(rs.getTimestamp("dateAjout"));
                }
            }
        }
    }

    private ParcelleCulture mapResultSetToParcelleCulture(ResultSet rs) throws SQLException {
        Parcelle parcelle = new Parcelle();
        parcelle.setId(rs.getInt("parcelleId"));
        parcelle.setNom(rs.getString("parcelle_nom"));

        Culture culture = new Culture();
        culture.setId(rs.getInt("cultureId"));
        culture.setNom(rs.getString("culture_nom"));

        Maladie maladie = null;
        if (rs.getObject("maladieId") != null) {
            maladie = new Maladie();
            maladie.setId(rs.getInt("maladieId"));
            maladie.setNom(rs.getString("maladie_nom"));
            maladie.setDescription(rs.getString("maladie_desc"));
        }

        return new ParcelleCulture(
                rs.getInt("id"),
                parcelle,
                culture,
                rs.getString("statut"),
                maladie,
                rs.getTimestamp("dateAjout")
        );
    }

    private ParcelleCulture currentEditingParcelleCulture;

    public ParcelleCulture getCurrentEditingParcelleCulture() {
        return currentEditingParcelleCulture;
    }

    public void setCurrentEditingParcelleCulture(ParcelleCulture parcelleCulture) {
        this.currentEditingParcelleCulture = parcelleCulture;
    }

}