package projet_gui.Controllers;

import projet_gui.Entities.Meteo;
import projet_gui.Services.AuthService;
import projet_gui.Services.MeteoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PageWeatherController extends ControllerBaseWithSidebar {

    @FXML
    private TextField citySearchField;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label conditionsLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private VBox alertsContainer;


    @FXML
    private VBox historyContainer;
    @FXML


    private final String apiKey = "c6977be568c002c754a19f650e13c65c";
    private MeteoService service = new MeteoService();

    @Override
    protected void initializePageContent() {
        historyContainer.getChildren().add(new Label("Test Weather History"));
         afficherHistorique();
    }

    @FXML
    private void afficherHistorique() {
        int nbFois = 5;

        List<Meteo> historique = service.getWeatherHistoryFromDb(nbFois); // Remplace l'appel à l'API
        System.out.println("Historique récupéré : " + historique.size());

        historyContainer.getChildren().clear(); // Vide le contenu précédent

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Meteo m : historique) {
            Label label = new Label(
                    formatter.format(m.getDate()) + " | " +
                            "Température: " + m.getTemperature() + "°C, " +
                            "Humidité: " + m.getHumidite() + "%, " +
                            "Conditions: " + m.getConditions()
            );
            historyContainer.getChildren().add(label);
        }
    }

    // Méthode pour afficher les alertes
    private void afficherAlertes(Meteo m) {
        if (m == null) return;

        StringBuilder message = new StringBuilder();

        // Vérifications des conditions météorologiques
        if (m.getTemperature() >= 40) {
            message.append("⚠️ Canicule : température élevée\n");
        } else if (m.getTemperature() <= 4) {
            message.append("❄️ Gel : température très basse\n");
        }


        if (m.getHumidite() >= 90) {
            message.append("💧 Humidité élevée\n");
        }

        String conditions = m.getConditions().toLowerCase();
        if (conditions.contains("pluie") || conditions.contains("rain")) {
            message.append("🌧️ Risque de pluie\n");
        }
        if (conditions.contains("neige") || conditions.contains("snow")) {
            message.append("❄️ Neige attendue\n");
        }
        if (conditions.contains("orage") || conditions.contains("thunderstorm")) {
            message.append("⛈️ Orage en approche\n");
        }

        // Affichage des alertes dans l'interface utilisateur
        alertsContainer.getChildren().clear(); // Vider les alertes précédentes
        if (message.length() > 0) {
            Label alertLabel = new Label(message.toString());
            alertLabel.setStyle("-fx-padding: 10; -fx-background-color: #2d4b36; -fx-border-color: #cccccc;");
            alertsContainer.getChildren().add(alertLabel);
        } else {
            // Si aucune alerte, afficher un message d'absence d'alerte
            Label alertLabel = new Label("Aucune alerte météo.");
            alertLabel.setStyle("-fx-padding: 10; -fx-background-color: #2d4b36; -fx-border-color: #cccccc;");
            alertsContainer.getChildren().add(alertLabel);
        }
    }

    @FXML
   private void refreshWeather(ActionEvent event) {
       String cityName = citySearchField.getText().trim();
       if (cityName.isEmpty()) {
           System.out.println("Veuillez entrer un nom de ville.");
           return;
       }

       Meteo meteoData = service.recupererMeteo(cityName, apiKey);
       if (meteoData != null) {
           temperatureLabel.setText(String.format("%.1f °C", meteoData.getTemperature()));
           humidityLabel.setText(String.format("%.0f %%", meteoData.getHumidite()));
           conditionsLabel.setText(meteoData.getConditions());
           dateLabel.setText(meteoData.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
           afficherEtEnregistrerMeteo(cityName);
           afficherAlertes(meteoData);
           afficherHistorique();

       } else {
           temperatureLabel.setText("Erreur");
           humidityLabel.setText("Erreur");
           conditionsLabel.setText("Erreur");
           dateLabel.setText("Erreur");
       }
   }


    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }
    @FXML
    private void afficherEtEnregistrerMeteo(String cityName) {
        String cleApi = "c6977be568c002c754a19f650e13c65c";

        Meteo m = service.recupererMeteo(cityName, cleApi);

        if (m != null) {
            // Affichage dans l'interface
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Label label = new Label(
                    formatter.format(LocalDateTime.now()) + " | " +
                            "Température: " + m.getTemperature() + "°C, " +
                            "Humidité: " + m.getHumidite() + "%, " +
                            "Conditions: " + m.getConditions()
            );
            historyContainer.getChildren().clear();
            historyContainer.getChildren().add(label);

            // Enregistrement dans la base
            try {
                Integer parcelleId = null; // ou une valeur réelle si tu veux la lier à une parcelle spécifique
                boolean success = service.addservice(m, parcelleId);
                if (success) {
                    System.out.println("Météo enregistrée avec succès.");
                } else {
                    System.out.println("Échec de l'enregistrement de la météo.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : météo introuvable pour la ville " + cityName);
        }
    }


}


