package projet_gui.Services;

import projet_gui.Entities.Maladie;
import projet_gui.Utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaladieService {
    private static MaladieService instance;

    private MaladieService() {}

    public static synchronized MaladieService getInstance() {
        if (instance == null) {
            instance = new MaladieService();
        }
        return instance;
    }

    public Maladie getMaladieById(int id) throws SQLException {
        String query = "SELECT * FROM Maladie WHERE id = ?";
        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Maladie maladie = new Maladie();
                    maladie.setId(rs.getInt("id"));
                    maladie.setNom(rs.getString("nom"));
                    maladie.setDescription(rs.getString("description"));
                    // Ajoutez d'autres champs si nécessaire
                    return maladie;
                }
            }
        }
        return null;
    }

    public List<Maladie> getAllMaladies() throws SQLException {
        List<Maladie> maladies = new ArrayList<>();
        String query = "SELECT * FROM Maladie";

        try (Connection conn = DataSource.getInstance().getCon();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Maladie m = new Maladie();
                m.setId(rs.getInt("id"));
                m.setNom(rs.getString("nom"));
                m.setDescription(rs.getString("description"));
                maladies.add(m);
            }
        }
        return maladies;
    }

}