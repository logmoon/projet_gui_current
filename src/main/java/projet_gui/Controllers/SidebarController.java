package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import projet_gui.App;
import projet_gui.Services.AuthService;

public class SidebarController {
    @FXML
    private VBox sidebar;

    @FXML
    private void navigateToDashboard() {
        App.navigateTo("page_dashboard");
    }

    @FXML
    private void navigateToTasks() {
        App.navigateTo("page_tasks");
    }

    @FXML
    private void navigateToFields() {
        App.navigateTo("page_fields");
    }

    @FXML
    private void navigateToCrops() {
        App.navigateTo("page_crops");
    }

    @FXML
    private void navigateToFieldCropAssociations() {
        App.navigateTo("page_fieldcropassociation");
    }

    @FXML
    private void navigateToWeather() {
        App.navigateTo("page_weather");
    }

    @FXML
    private void navigateToDiseases() {
        App.navigateTo("page_diseases");
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