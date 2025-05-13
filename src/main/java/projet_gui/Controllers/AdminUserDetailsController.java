package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import projet_gui.App;
import projet_gui.Entities.Utilisateur;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.DataStore;

import java.sql.SQLException;
import java.util.Optional;

public class AdminUserDetailsController extends ControllerBaseWithSidebar {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label statusLabel;

    private UtilisateurService utilisateurService;
    private Utilisateur selectedUser;

    @Override
    public boolean canEnter() {
        AuthService authService = AuthService.getInstance();
        return authService.isAuthenticated() && 
               authService.getCurrentToken() != null && 
               authService.getCurrentToken().getUser().isAdmin();
    }

    @Override
    protected void initializePageContent() {
        utilisateurService = UtilisateurService.getInstance();
        
        // Get the selected user ID from DataStore
        Integer selectedUserId = DataStore.get("selectedUserId", Integer.class);
        if (selectedUserId == null) {
            // If no user ID is found, go back to the dashboard
            App.navigateTo("admin_dashboard");
            return;
        }
        
        loadUserDetails(selectedUserId);
    }

    private void loadUserDetails(int userId) {
        try {
            Optional<Utilisateur> user = utilisateurService.getUserById(userId);
            if (user.isPresent()) {
                selectedUser = user.get();
                nameLabel.setText(selectedUser.getPrenom() + " " + selectedUser.getNom());
                emailLabel.setText(selectedUser.getEmail());
                statusLabel.setText(selectedUser.getActif() ? "Active" : "Inactive");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        App.navigateTo("admin_dashboard");
    }
}