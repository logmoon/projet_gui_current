package projet_gui.Services;

import projet_gui.Entities.Role;
import projet_gui.Entities.Utilisateur;
import projet_gui.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurService {
    
    private Connection connection;
    private static UtilisateurService instance;
    
    // Singleton pattern
    private UtilisateurService() {
        connection = DataSource.getInstance().getCon();
    }
    
    public static UtilisateurService getInstance() {
        if (instance == null) {
            instance = new UtilisateurService();
        }
        return instance;
    }
    
    /**
     * Register a new user
     * @return true if registration successful, false otherwise
     */
    public boolean register(String nom, String prenom, String email, String password, Role role) throws SQLException {
        // Check if email already exists
        if (emailExists(email)) {
            throw new SQLException("Email already registered");
        }
        
        String query = "INSERT INTO utilisateur (nom, prenom, email, motDePasse, role, actif) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Create temporary user object to validate and hash password
            Utilisateur tempUser = createUser(0, nom, prenom, email, password, role, true);
            
            pst.setString(1,  tempUser.getNom());
            pst.setString(2,  tempUser.getPrenom());
            pst.setString(3,  tempUser.getEmail());
            pst.setString(4,  tempUser.getPwdHash());
            pst.setString(5,  tempUser.getRole().name());
            pst.setBoolean(6, tempUser.getActif());
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            throw new SQLException("Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate a user
     * @return Optional with User if authentication successful, empty Optional otherwise
     */
    public Optional<Utilisateur> login(String email, String password) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE email = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Create temporary user to hash provided password
                    Utilisateur tempUser = new Utilisateur();
                    tempUser.setPwd(password);
                    String hashedInputPassword = tempUser.getPwdHash();
                    
                    // Compare with stored hash
                    String storedHash = rs.getString("motDePasse");
                    if (storedHash.equals(hashedInputPassword)) {
                        // Password matches, return the user
                        return Optional.of(extractUserFromResultSet(rs));
                    }
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if email already exists in database
     */
    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get user by ID
     */
    public Optional<Utilisateur> getUserById(int id) throws SQLException {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUserFromResultSet(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all users
     */
    public List<Utilisateur> getAllUsers() throws SQLException {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT * FROM utilisateur";
        
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Update user information
     */
    public boolean updateUser(Utilisateur user) throws SQLException {
        String query = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, role = ?, actif = ? WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, user.getNom());
            pst.setString(2, user.getPrenom());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getRole().name());
            pst.setBoolean(5, user.getActif());
            pst.setInt(6, user.getId());
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        // First verify old password
        Optional<Utilisateur> userOpt = getUserById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        Utilisateur user = userOpt.get();
        
        // Create temporary user to hash old password for comparison
        Utilisateur tempUser = new Utilisateur();
        tempUser.setPwd(oldPassword);
        String hashedOldPassword = tempUser.getPwdHash();
        
        // Compare with stored hash
        if (!user.getPwdHash().equals(hashedOldPassword)) {
            return false; // Old password doesn't match
        }
        
        // Update with new password
        String query = "UPDATE utilisateur SET motDePasse = ? WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Create temporary user to hash new password
            tempUser.setPwd(newPassword);
            String hashedNewPassword = tempUser.getPwdHash();
            
            pst.setString(1, hashedNewPassword);
            pst.setInt(2, userId);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Verify user password
     */
    public boolean verifyPassword(int userId, String password) throws SQLException {
        Optional<Utilisateur> userOpt = getUserById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        Utilisateur user = userOpt.get();
        System.out.println(user);
        
        // Create temporary user to hash password for comparison
        Utilisateur tempUser = new Utilisateur();
        tempUser.setPwd(password);
        String hashedPassword = tempUser.getPwdHash();
        System.out.println(hashedPassword);
        
        // Compare with stored hash
        System.out.println(user.getPwdHash());
        return user.getPwdHash().equals(hashedPassword);
    }
    
    /**
     * Update user password directly
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String query = "UPDATE utilisateur SET motDePasse = ? WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            // Create temporary user to hash new password
            Utilisateur tempUser = new Utilisateur();
            tempUser.setPwd(newPassword);
            String hashedNewPassword = tempUser.getPwdHash();
            
            pst.setString(1, hashedNewPassword);
            pst.setInt(2, userId);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deactivate user account
     */
    public boolean deactivateUser(int userId) throws SQLException {
        String query = "UPDATE utilisateur SET actif = false WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Activate user account
     */
    public boolean activateUser(int userId) throws SQLException {
        String query = "UPDATE utilisateur SET actif = true WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM utilisateur WHERE id = ?";
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, userId);
            
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Helper method to extract user from ResultSet
    public Utilisateur extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String email = rs.getString("email");
        Role role = Role.valueOf(rs.getString("role"));
        String pwdHash = rs.getString("motDePasse");
        boolean actif = rs.getBoolean("actif");
        
        return createUser(id, nom, prenom, email, pwdHash, role, actif);
    }
    
    // Helper method to create user instance
    private Utilisateur createUser(int id, String nom, String prenom, String email, String pwdHash, Role role, boolean actif) {
        Utilisateur user = new Utilisateur();
        
        if (id > 0) {
            user.setId(id);
        }
        
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setHashedPwd(pwdHash);
        user.setPrenom(prenom);
        user.setRole(role);
        user.setActif(actif);
        
        return user;
    }
}