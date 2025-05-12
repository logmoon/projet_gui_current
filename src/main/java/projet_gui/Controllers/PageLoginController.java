package projet_gui.Controllers;

import java.sql.SQLException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projet_gui.Services.AuthService;
import projet_gui.Services.JournalConnexionService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.Alerts;
import projet_gui.App;
import projet_gui.Entities.Utilisateur;

import javafx.application.Platform;

public class PageLoginController extends ControllerBase {

    private UtilisateurService utilisateurService;
    private AuthService authService;

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated() == false;
    }

    @Override
    public void initializePageContent() {
        // Get the service instance
        utilisateurService = UtilisateurService.getInstance();
        authService = AuthService.getInstance();
        
        // Try logging in from saved token
        try {
            if (authService.attemptAutoLogin()) {
                
                // We use this to make the setRoot function run after the UI is displayed
                // Without it the setRoot won't change the scene.
                // TODO: Maybe find a better way to do this?
                Platform.runLater(() -> {
                    App.navigateTo("page_dashboard");
                });
            }
        }
        catch(Exception e) {
        }
    }



    @FXML
    private TextField emailField;

    @FXML
    private Button inscrireButton;

    @FXML
    private PasswordField pwdField;

    @FXML
    void createAccount(ActionEvent event) {
        App.navigateTo("page_inscription");
    }

    @FXML
    private void recoverPassword(ActionEvent event) {
        App.navigateTo("page_forgot_password");
    }

    @FXML
    void inscrire(ActionEvent event) {
        // Validate the inputs
        if (validateInputs()) {
            try {
                // Get values from fields
                String email = emailField.getText().trim();
                String password = pwdField.getText();

                // Login the user
                Optional<Utilisateur> userOpt = utilisateurService.login(email, password);

                // Kind of an unnecessary check, but it's fine
                if (userOpt.isPresent()) {
                    Utilisateur user = userOpt.get();
                    boolean authenticated = authService.createAuthToken(user);
                    if (authenticated) {
                        // Save the token to file
                        authService.saveTokenToFile(authService.getCurrentToken().getToken());
                        // Log the connection, for now, we'll pass an empty ip address
                        JournalConnexionService.getInstance().logConnection(user, "");
                    }
                    else {
                        Alerts.showAlert(Alert.AlertType.ERROR, "Erreur de login", 
                            "Couldn't authenticated user: " + user.getNom());
                    }

                    Alerts.showAlert(Alert.AlertType.INFORMATION, "Login réussite", 
                        "Bienvenue " + user.getNom());
                    
                    clearFields();

                    App.navigateTo("page_dashboard", "admin_dashboard");
                }
                else {
                    // We shouldn't be able to get here, but yeah
                    Alerts.showAlert(Alert.AlertType.ERROR, "Erreur de login", 
                        "Couldn't find user");
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
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorMessage.append("L'email ne peut pas être vide.\n");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMessage.append("Format d'email invalide.\n");
        }
        
        if (pwdField.getText().trim().isEmpty()) {
            errorMessage.append("Le mot de passe ne peut pas être vide.\n");
        }

        if (errorMessage.length() > 0) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void clearFields() {
        emailField.clear();
        pwdField.clear();
    }
}