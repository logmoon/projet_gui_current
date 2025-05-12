package projet_gui.Services;

import projet_gui.Utils.DataSource;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class PasswordResetService {
    
    private Connection connection;
    private static PasswordResetService instance;
    private final UtilisateurService utilisateurService;
    
    // Singleton pattern
    private PasswordResetService() {
        connection = DataSource.getInstance().getCon();
        utilisateurService = UtilisateurService.getInstance();
    }
    
    public static PasswordResetService getInstance() {
        if (instance == null) {
            instance = new PasswordResetService();
        }
        return instance;
    }
    
    /**
     * Generate a verification code for password reset
     * @param email The email address to send the code to
     * @return The generated verification code
     */
    public String generateVerificationCode(String email) throws SQLException {
        // Check if email exists
        if (!utilisateurService.emailExists(email)) {
            throw new SQLException("Email not found");
        }
        
        // Generate a random 6-digit code
        String code = generateRandomCode(15);
        
        // Calculate expiration time (24 hours from now)
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusHours(24));
        
        // First invalidate any existing codes for this email
        invalidateExistingCodes(email);
        
        // Insert the new code
        String query = "INSERT INTO ReinitialisationMotDePasse (email, codeVerification, dateExpiration, codeValide) VALUES (?, ?, ?, ?)"; 
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, code);
            pst.setTimestamp(3, expirationTime);
            pst.setBoolean(4, true);
            
            pst.executeUpdate();
            
            // TODO: Send the verification code via email
            
            return code;
        }
    }
    
    /**
     * Validate a verification code
     * @param email The email address
     * @param code The verification code to validate
     * @return true if the code is valid, false otherwise
     */
    public boolean validateVerificationCode(String email, String code) throws SQLException {
        String query = "SELECT * FROM ReinitialisationMotDePasse WHERE email = ? AND codeVerification = ? AND codeValide = true AND dateExpiration > ?"; 
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, code);
            pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // If there's a result, the code is valid
            }
        }
    }
    
    /**
     * Reset a user's password
     * @param email The email address
     * @param code The verification code
     * @param newPassword The new password
     * @return true if the password was reset successfully, false otherwise
     */
    public boolean resetPassword(String email, String code, String newPassword) throws SQLException {
        // First validate the code
        if (!validateVerificationCode(email, code)) {
            return false;
        }
        
        // Update the password
        boolean updated = utilisateurService.updatePassword(email, newPassword);
        
        if (updated) {
            // Invalidate the used code
            invalidateCode(email, code);
        }
        
        return updated;
    }
    
    /**
     * Invalidate all existing codes for an email
     */
    private void invalidateExistingCodes(String email) throws SQLException {
        String query = "UPDATE ReinitialisationMotDePasse SET codeValide = false WHERE email = ?"; 
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            pst.executeUpdate();
        }
    }
    
    /**
     * Invalidate a specific code
     */
    private void invalidateCode(String email, String code) throws SQLException {
        String query = "UPDATE ReinitialisationMotDePasse SET codeValide = false WHERE email = ? AND codeVerification = ?"; 
        
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, email);
            pst.setString(2, code);
            pst.executeUpdate();
        }
    }
    
    /**
     * Generate a random code of specified length
     */
    private String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        
        return sb.toString();
    }
}