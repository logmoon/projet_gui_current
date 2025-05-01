package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projet_gui.App;
import projet_gui.Entities.Role;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.Alerts;

import java.sql.SQLException;

public class PageInscriptionController extends ControllerBase {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField pwdField;
    
    @FXML
    private Button inscrireButton;
    
    private UtilisateurService utilisateurService;
    
    @Override
    public void initializePageContent() {
        // Get the service instance
        utilisateurService = UtilisateurService.getInstance();
    }


    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated() == false;
    }
    
    @FXML
    private void login(ActionEvent event) {
        try {
            App.navigateTo("page_login");
        }
        catch (Exception e) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Unknown Erreur", 
                "Unknown Error: " + e.getMessage());
        }
    }

    @FXML
    private void signupAsAdmin(ActionEvent event) {
    }
    
    @FXML
    private void inscrire(ActionEvent event) {
        // Validate inputs
        if (validateInputs()) {
            try {
                // Get values from fields
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email = emailField.getText().trim();
                String password = pwdField.getText();
                Role role = Role.AGRICULTEUR;
                
                // Register the user
                boolean registered = utilisateurService.register(nom, prenom, email, password, role);
                
                if (registered) {
                    Alerts.showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", 
                              "Compte créé avec succès pour " + prenom + " " + nom + ".\nPlease Log in!");
                    
                    // Clear the fields after successful registration
                    clearFields();

                    App.navigateTo("page_login");

                } else {
                    Alerts.showAlert(Alert.AlertType.ERROR, "Erreur d'inscription", 
                              "L'inscription a échoué pour une raison inconnue.");
                }
                
            } catch (SQLException e) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Erreur d'inscription", 
                          "Une erreur s'est produite: " + e.getMessage());
            } catch (Exception e) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Erreur d'inscription", 
                          "Une erreur s'est produite: " + e.getMessage());
            }
        }
    }
    
    private boolean validateInputs() {
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
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMessage.append("Format d'email invalide.\n");
        }
        
        if (pwdField.getText().trim().isEmpty()) {
            errorMessage.append("Le mot de passe ne peut pas être vide.\n");
        } else if (pwdField.getText().length() < 6) {
            errorMessage.append("Le mot de passe doit contenir au moins 6 caractères.\n");
        }
        
        if (errorMessage.length() > 0) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        pwdField.clear();
    }
}