package projet_gui.Controllers;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projet_gui.App;
import projet_gui.Entities.AuthToken;
import projet_gui.Entities.Utilisateur;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.Alerts;

public class PageUpdateAccountController extends ControllerBaseWithSidebar {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField currentPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    private AuthService authService;
    private UtilisateurService utilisateurService;
    private Utilisateur currentUser;
    
    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }
    
    @Override
    public void initializePageContent() {
        authService = AuthService.getInstance();
        utilisateurService = UtilisateurService.getInstance();
        
        // Get current user and populate fields
        AuthToken token = authService.getCurrentToken();
        if (token != null) {
            currentUser = token.getUser();
            populateFields();
        } else {
            // Fallback to login page if no user is authenticated
            App.navigateTo("page_login");
        }
    }
    
    private void populateFields() {
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
        }
    }
    
    @FXML
    private void navigateToProfile(ActionEvent event) {
        App.navigateTo("page_profile");
    }
    
    @FXML
    private void saveChanges(ActionEvent event) {
        if (validatePersonalInfo()) {
            try {
                // Update personal information
                boolean personalInfoUpdated = updatePersonalInfo();
                
                // Update password if provided
                boolean passwordUpdated = false;
                if (!currentPasswordField.getText().isEmpty()) {
                    passwordUpdated = updatePassword();
                }
                
                if (personalInfoUpdated || passwordUpdated) {
                    Alerts.showAlert(AlertType.INFORMATION, "Update Successful", 
                            "Your account information has been updated successfully.");
                    
                    // Refresh the current user data
                    try {
                        currentUser = utilisateurService.getUserById(currentUser.getId()).get();
                        // Update the auth token with the updated user
                        authService.refreshCurrentUser(currentUser);
                    } catch (SQLException e) {
                        Alerts.showAlert(AlertType.ERROR, "Error", 
                                "Failed to refresh user data: " + e.getMessage());
                    }
                    
                    // Navigate back to profile page
                    App.navigateTo("page_profile");
                }
            } catch (SQLException e) {
                Alerts.showAlert(AlertType.ERROR, "Update Failed", 
                        "Failed to update account information: " + e.getMessage());
            }
        }
    }
    
    private boolean validatePersonalInfo() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nomField.getText().trim().isEmpty()) {
            errorMessage.append("Le nom ne peut pas être vide.\n");
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            errorMessage.append("Le prénom ne peut pas être vide.\n");
        }
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorMessage.append("L'email ne peut pas être vide.\n");
        } else if (!email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMessage.append("Format d'email invalide.\n");
        }
        
        // Validate password fields if the user is trying to change password
        if (!currentPasswordField.getText().isEmpty()) {
            if (newPasswordField.getText().isEmpty()) {
                errorMessage.append("Le nouveau mot de passe ne peut pas être vide.\n");
            } else if (newPasswordField.getText().length() < 6) {
                errorMessage.append("Le nouveau mot de passe doit contenir au moins 6 caractères.\n");
            }
            
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                errorMessage.append("Les mots de passe ne correspondent pas.\n");
            }
        }
        
        if (errorMessage.length() > 0) {
            Alerts.showAlert(AlertType.ERROR, "Erreur de validation", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean updatePersonalInfo() throws SQLException {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        
        // Check if any field has changed
        if (nom.equals(currentUser.getNom()) && 
            prenom.equals(currentUser.getPrenom()) && 
            email.equals(currentUser.getEmail())) {
            return false; // No changes to personal info
        }
        
        // Check if email is already taken by another user
        if (!email.equals(currentUser.getEmail()) && utilisateurService.emailExists(email)) {
            Alerts.showAlert(AlertType.ERROR, "Email Already Exists", 
                    "This email is already registered to another account.");
            return false;
        }
        
        // Update user information
        currentUser.setNom(nom);
        currentUser.setPrenom(prenom);
        currentUser.setEmail(email);
        
        return utilisateurService.updateUser(currentUser);
    }
    
    private boolean updatePassword() throws SQLException {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        
        // Verify current password
        if (!utilisateurService.verifyPassword(currentUser.getId(), currentPassword)) {
            Alerts.showAlert(AlertType.ERROR, "Incorrect Password", 
                    "The current password you entered is incorrect.");
            return false;
        }
        
        // Update password
        return utilisateurService.updatePassword(currentUser.getId(), newPassword);
    }
}