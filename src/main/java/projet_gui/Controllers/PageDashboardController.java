package projet_gui.Controllers;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Tache;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Services.ParcelleService;
import projet_gui.Services.TacheService;
import projet_gui.Utils.Alerts;

public class PageDashboardController extends ControllerBaseWithSidebar {

    @FXML
    private Label totalFieldsLabel;

    @FXML
    private Label activeCropsLabel;

    @FXML
    private Label pendingTasksLabel;

    @FXML
    private VBox urgentTasksContainer;

    private ParcelleService parcelleService;
    private CultureService cultureService;
    private TacheService tacheService;

    @Override
    public void initializePageContent() {
        parcelleService = ParcelleService.getInstance();
        cultureService = CultureService.getInstance();
        tacheService = TacheService.getInstance();

        // Initialize dashboard data
        loadDashboardData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated() &&
               AuthService.getInstance().getCurrentToken().getUser().isAdmin() == false;
    }

    private void loadDashboardData() {
        try {
            // Parcelles
            List<Parcelle> parcelles = parcelleService.getAllByUserId(AuthService.getInstance().getCurrentToken().getUser().getId());
            totalFieldsLabel.setText(String.valueOf(parcelles.size()));

            // Cultures
            List<Culture> cultures = cultureService.getAllByUserId(AuthService.getInstance().getCurrentToken().getUser().getId());
            activeCropsLabel.setText(String.valueOf(cultures.size()));

            // Taches
            List<Tache> taches = tacheService.getNotCompletedAllByUserId(AuthService.getInstance().getCurrentToken().getUser().getId());
            pendingTasksLabel.setText(String.valueOf(taches.size()));

            // Display urgent tasks
            for (Tache tache : taches) {
                if (tache.getPriorite().equals(Tache.PRIORITE_URGENT)) {
                    String taskText = String.format("%s - Field: [ %s ] - Status: [ %s ]",
                            tache.getDescription(),
                            tache.getParcelle().getNom(),
                            tache.getStatut());
                    Label taskLabel = new Label(taskText);
                    taskLabel.setStyle("fx-font-weight: bold;");
                    urgentTasksContainer.getChildren().add(taskLabel);
                }
            }
        }
        catch (Exception e) {
            Alerts.showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void addNewField(ActionEvent event) {
        App.navigateTo("page_fields");
    }

    @FXML
    private void addNewTask(ActionEvent event) {
        App.navigateTo("page_tasks");
    }

    @FXML
    private void viewWeather(ActionEvent event) {
        App.navigateTo("page_weather");
    }
}