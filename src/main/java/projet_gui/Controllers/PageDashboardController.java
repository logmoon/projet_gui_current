package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import projet_gui.App;
import projet_gui.Services.AuthService;

public class PageDashboardController extends ControllerBaseWithSidebar {

    @FXML
    private Label totalFieldsLabel;

    @FXML
    private Label activeCropsLabel;

    @FXML
    private Label pendingTasksLabel;

    @FXML
    private VBox fieldStatusContainer;

    @FXML
    private VBox weatherAlertsContainer;

    @Override
    public void initializePageContent() {
        // Initialize dashboard data
        loadDashboardData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void loadDashboardData() {
        // TODO: Load actual data from services
        // This will be implemented by the business logic team
    }

    @FXML
    private void addNewField(ActionEvent event) {
        // TODO: Implement new field creation
        App.navigateTo("page_field_add");
    }

    @FXML
    private void addNewTask(ActionEvent event) {
        // TODO: Implement new task creation
        App.navigateTo("page_tasks");
    }

    @FXML
    private void viewWeather(ActionEvent event) {
        // TODO: Implement weather view navigation
        App.navigateTo("page_weather");
    }
}