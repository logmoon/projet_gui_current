package projet_gui.Services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import projet_gui.Entities.AuthToken;
import projet_gui.Entities.Utilisateur;
import projet_gui.Utils.DataSource;

public class AuthService {
    private Connection connection;
    private static AuthService instance;

    private AuthToken currentToken;
    public AuthToken getCurrentToken() { return currentToken; }
    public boolean isAuthenticated() { return currentToken != null; }
    
    /**
     * Refreshes the current user data in the token
     * Used after user information has been updated
     */
    public void refreshCurrentUser(Utilisateur updatedUser) {
        if (currentToken != null) {
            currentToken.setUser(updatedUser);
        }
    }

    // Singleton pattern
    private AuthService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private String generateRandomToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public boolean attemptAutoLogin() throws SQLException {
        String token = loadTokenFromFile();

        if (token != null && validateToken(token)) {
            Optional<Utilisateur> userOpt = loginWithToken(token);
            if (userOpt.isPresent()) {
                Utilisateur user = userOpt.get();

                // Fetch the full token data again from the database
                String query = "SELECT * FROM authtoken WHERE token = ?";
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, token);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int id = rs.getInt("id");
                            Timestamp dateCreation = rs.getTimestamp("dateCreation");
                            Timestamp dateExpiration = rs.getTimestamp("dateExpiration");
                            boolean estActif = rs.getBoolean("estActif");

                            // Now rebuild the currentToken properly
                            currentToken = new AuthToken(id, user, token, dateCreation, dateExpiration, estActif);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public Optional<Utilisateur> loginWithToken(String token) throws SQLException {
        String query = "SELECT u.* FROM authtoken at " +
                    "JOIN utilisateur u ON at.utilisateurId = u.id " +
                    "WHERE at.token = ? AND at.estActif = 1 AND at.dateExpiration > CURRENT_TIMESTAMP";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, token);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Assuming you have a method to create a Utilisateur from ResultSet
                    Utilisateur user = UtilisateurService.getInstance().extractUserFromResultSet(rs);
                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    public boolean createAuthToken(Utilisateur user) throws SQLException {
        // First, check if a valid token already exists for the user
        String selectQuery = "SELECT * FROM authtoken WHERE utilisateurId = ? AND estActif = 1 AND dateExpiration > NOW()";

        try (PreparedStatement selectPst = connection.prepareStatement(selectQuery)) {
            selectPst.setInt(1, user.getId());

            try (ResultSet rs = selectPst.executeQuery()) {
                if (rs.next()) {
                    // Existing active token found, update it
                    int tokenId = rs.getInt("id");
                    String newToken = generateRandomToken();
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    // We'll do 1 hour validity for now
                    Timestamp expiration = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));

                    String updateQuery = "UPDATE authtoken SET token = ?, dateCreation = ?, dateExpiration = ? WHERE id = ?";
                    try (PreparedStatement updatePst = connection.prepareStatement(updateQuery)) {
                        updatePst.setString(1, newToken);
                        updatePst.setTimestamp(2, now);
                        updatePst.setTimestamp(3, expiration);
                        updatePst.setInt(4, tokenId);
                        updatePst.executeUpdate();
                    }

                    currentToken = new AuthToken(tokenId, user, newToken, now, expiration, true);
                    return true;
                }
            }
        }

        // No valid token, create a new one
        String insertQuery = "INSERT INTO authtoken (utilisateurId, token, dateCreation, dateExpiration, estActif) VALUES (?, ?, ?, ?, ?)";

        String token = generateRandomToken();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // We'll do 1 hour validity for now
        Timestamp expiration = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));

        try (PreparedStatement insertPst = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertPst.setInt(1, user.getId());
            insertPst.setString(2, token);
            insertPst.setTimestamp(3, now);
            insertPst.setTimestamp(4, expiration);
            insertPst.setBoolean(5, true);

            insertPst.executeUpdate();

            try (ResultSet generatedKeys = insertPst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    currentToken = new AuthToken(newId, user, token, now, expiration, true);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean validateToken(String token) {
        String query = "SELECT * FROM authtoken WHERE token = ? AND estActif = 1 AND dateExpiration > NOW()";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, token);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // If a valid token exists, return true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void invalidateToken(String token) {
        String query = "UPDATE authtoken SET estActif = 0 WHERE token = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, token);
            pst.executeUpdate();

            // Also clear the current token if it's the one being invalidated
            if (currentToken != null && currentToken.getToken().equals(token)) {
                currentToken = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        if (currentToken != null) {
            invalidateToken(currentToken.getToken());
            currentToken = null;
        }
        deleteTokenFile();
    }

    private void deleteTokenFile() {
        File file = new File("auth_token.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    public String loadTokenFromFile() {
        File file = new File("auth_token.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.readLine(); // Assuming the token is in the first line
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void saveTokenToFile(String token) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("auth_token.txt"))) {
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
