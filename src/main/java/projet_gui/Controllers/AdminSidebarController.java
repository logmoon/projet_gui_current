package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import projet_gui.App;
import projet_gui.Services.AuthService;

public class AdminSidebarController {
    @FXML
    private VBox sidebar;

    @FXML
    private void navigateToDashboard() {
        App.navigateTo("admin_dashboard");
    }

    @FXML
    private void navigateToProfile() {
        App.navigateTo("page_profile");
    }

    @FXML
    private void logout() {
        AuthService.getInstance().logout();
        App.navigateTo("page_login");
    }
}