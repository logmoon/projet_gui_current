package projet_gui.Controllers;

import java.sql.SQLException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import projet_gui.App;
import projet_gui.Entities.AuthToken;
import projet_gui.Entities.Utilisateur;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.Alerts;
import javafx.application.Platform;

public class PageProfileController extends ControllerBaseWithSidebar {

    private AuthService authService;
    private UtilisateurService utilisateurService;

    @FXML
    private Label userName;

    @FXML
    private Label role;

    @FXML
    private Label userInitials;


    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private String getInitials(Utilisateur user) {
        String nom = user.getNom();
        String prenom = user.getPrenom();
        
        char initialNom = nom.isEmpty() ? '?' : Character.toUpperCase(nom.charAt(0));
        char initialPrenom = prenom.isEmpty() ? '?' : Character.toUpperCase(prenom.charAt(0));
        
        return "" + initialPrenom + initialNom;
    }

    private void setup(Utilisateur user) {
        userName.setText(user.getNom());
        userInitials.setText(getInitials(user));
        role.setText(user.isAdmin() ? "Admin" : "Farmer");
    }

    @Override
    public void initializePageContent() {
        authService = AuthService.getInstance();
        utilisateurService = UtilisateurService.getInstance();

        AuthToken token = authService.getCurrentToken();
        if (token != null) {
            Utilisateur user = token.getUser();
            setup(user);
        }
        else {
            // Fallback
            try {
                if (authService.attemptAutoLogin()) {
                    token = authService.getCurrentToken();
                    if (token != null) {
                        Utilisateur user = token.getUser();
                        setup(user);
                    }
                }
                else
                {
                    // We use this to make the setRoot function run after the UI is displayed
                    // Without it the setRoot won't change the scene.
                    // TODO: Maybe find a better way to do this?
                    Platform.runLater(() -> {
                        App.navigateTo("page_login");
                    });
                }
            }
            catch(Exception e) {
            }
        }
    }

    @FXML
    private void editAccount(ActionEvent event) {
    }

    @FXML
    private void deleteAccount(ActionEvent event) {
        Optional<ButtonType> result = Alerts.showAlert(AlertType.CONFIRMATION, "Delete Account", "Are you sure you want to delete your account?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User clicked OK
            Utilisateur user = authService.getCurrentToken().getUser();
            try {
                utilisateurService.deleteUser(user.getId());
                authService.logout();
                App.navigateTo("page_login");
            }
            catch (SQLException e) {
                Alerts.showAlert(AlertType.ERROR, "Account Deletion Error",
                    "An error occured when deleting the account: " + e.getMessage());
            }
        }
    }
}
