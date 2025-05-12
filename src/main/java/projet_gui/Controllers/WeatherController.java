// src/main/java/projet_gui/Controllers/WeatherController.java
package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import projet_gui.Entities.WeatherData;
import projet_gui.Services.WeatherService;

public class WeatherController {

    @FXML private TextField cityField;
    @FXML private Button getWeatherBtn;
    @FXML private Label weatherResult;
    @FXML private ImageView weatherIcon;
    @FXML private Label temperatureLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windLabel;

    @FXML
    public void initialize() {
        getWeatherBtn.setOnAction(e -> {
            String city = cityField.getText().trim();
            if (!city.isEmpty()) {
                WeatherData data = WeatherService.getWeatherData(city);
                updateWeatherDisplay(data);
            } else {
                weatherResult.setText("Veuillez entrer une ville");
                clearWeatherData();
            }
        });
    }

    private void updateWeatherDisplay(WeatherData data) {
        if (data == null) {
            weatherResult.setText("Impossible de récupérer les données météo");
            clearWeatherData();
            return;
        }

        weatherResult.setText(data.getDescription());
        temperatureLabel.setText(String.format("%.1f°C", data.getTemperature()));
        humidityLabel.setText(String.format("%.0f%%", data.getHumidity()));
        windLabel.setText(String.format("%.1f km/h", data.getWindSpeed()));

        setWeatherIcon(data.getDescription());
    }

    private void clearWeatherData() {
        temperatureLabel.setText("--");
        humidityLabel.setText("--");
        windLabel.setText("--");
        weatherIcon.setImage(null);
    }

    private void setWeatherIcon(String description) {
        String iconName;
        String lowerDesc = description.toLowerCase();

        if (lowerDesc.contains("cloud")) {
            iconName = "cloud.png";
        } else if (lowerDesc.contains("rain") || lowerDesc.contains("shower")) {
            iconName = "rain.png";
        } else if (lowerDesc.contains("sun") || lowerDesc.contains("clear")) {
            iconName = "sun.png";
        } else if (lowerDesc.contains("snow")) {
            iconName = "snow.png";
        } else if (lowerDesc.contains("fog") || lowerDesc.contains("mist")) {
            iconName = "fog.png";
        } else {
            iconName = "default.png";
        }

        try {
            Image image = new Image(getClass().getResourceAsStream("/projet_gui/images/" + iconName));
            weatherIcon.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading weather icon: " + e.getMessage());
            weatherIcon.setImage(null);
        }
    }
}