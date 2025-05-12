package projet_gui.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private Connection con;
    private String url = "jdbc:mysql://localhost:3306/schema";
    private String user = "root";
    private String pass = "";

    private static DataSource data;

    private DataSource() {
        connect();
    }

    private void connect() {
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.err.println("Échec de la connexion: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données: " + e.getMessage());
        }
    }

    public Connection getCon() {
        try {
            if (con == null || con.isClosed()) {
                System.out.println("Connexion est null ou fermée, reconnexion...");
                connect();
                System.out.println("Reconnexion réussie");
            }
        } catch (SQLException e) {
            System.err.println("Échec de la vérification de la connexion: " + e.getMessage());
            throw new RuntimeException("Problème avec la connexion: " + e.getMessage());
        }
        return con;
    }

    public static DataSource getInstance() {
        if (data == null) {
            data = new DataSource();
        }
        return data;
    }

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Échec de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}