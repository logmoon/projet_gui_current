package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import projet_gui.Services.AuthService;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Meteo;

public class PageWeatherController extends ControllerBaseWithSidebar {

    @FXML
    private ComboBox<Parcelle> fieldSelector;

    @FXML
    private GridPane currentWeatherGrid;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label conditionsLabel;

    @FXML
    private VBox alertsContainer;

    @FXML
    private VBox historyContainer;

    @Override
    public void initializePageContent() {
        setupFieldSelector();
        loadWeatherData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFieldSelector() {
        // TODO: Load fields into selector
        // This will be implemented by the business logic team
    }

    private void loadWeatherData() {
        // TODO: Load weather data for selected field
        // This will be implemented by the business logic team
    }

    private void updateWeatherDisplay(Meteo weatherData) {
        // TODO: Update weather display with new data
        // This will be implemented by the business logic team
    }

    private void loadWeatherHistory() {
        // TODO: Load and display weather history
        // This will be implemented by the business logic team
    }

    private void loadWeatherAlerts() {
        // TODO: Load and display weather alerts
        // This will be implemented by the business logic team
    }

    @FXML
    private void refreshWeather(ActionEvent event) {
        loadWeatherData();
    }
}