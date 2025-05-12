package projet_gui.Services;

import projet_gui.Entities.Utilisateur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import projet_gui.Utils.DataSource;

public class JournalConnexionService {
    private Connection connection;
    private static JournalConnexionService instance;

    public JournalConnexionService() {
        connection = DataSource.getInstance().getCon();
    }

    public static JournalConnexionService getInstance() {
        if (instance == null) {
            instance = new JournalConnexionService();
        }
        return instance;
    }

    public void logConnection(Utilisateur user, String ipAddress) {
        String query = "INSERT INTO journalconnexion (utilisateurId, dateConnexion, adresseIP) VALUES (?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, user.getId());
            pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pst.setString(3, ipAddress);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
