package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Utils.FileDialogUtils;
import projet_gui.Utils.NotificationHelper;

import java.sql.SQLException;

public class PageCropAddController extends ControllerBaseWithSidebar {

    @FXML
    private TextField cropNameField;

    @FXML
    private TextField waterNeedsField;

    @FXML
    private TextField nutrientNeedsField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField imagePathField;

    @Override
    public void initializePageContent() {
        setupFormFields();
    }

    @Override
    public boolean canEnter() {
        boolean isAuthenticated = AuthService.getInstance().isAuthenticated();
        System.out.println("PageCropAddController - User authenticated: " + isAuthenticated);
        return isAuthenticated;
    }

    private void setupFormFields() {
        // Clear all fields
        cropNameField.clear();
        waterNeedsField.clear();
        nutrientNeedsField.clear();
        descriptionField.clear();
        imagePathField.clear();

        // Set prompt texts
        cropNameField.setPromptText("e.g., Wheat");
        waterNeedsField.setPromptText("Liters per hectare");
        nutrientNeedsField.setPromptText("Kg per hectare");
        descriptionField.setPromptText("Enter crop details...");
        imagePathField.setPromptText("Select an image file");

        // Add validation listeners
        cropNameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                cropNameField.setText(oldValue);
                showAlert(Alert.AlertType.WARNING, "Input Error", "Crop name cannot exceed 100 characters.");
            }
        });

        waterNeedsField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                waterNeedsField.setText(oldValue);
                showAlert(Alert.AlertType.WARNING, "Input Error", "Water needs must be a valid number.");
            }
        });

        nutrientNeedsField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                nutrientNeedsField.setText(oldValue);
                showAlert(Alert.AlertType.WARNING, "Input Error", "Nutrient needs must be a valid number.");
            }
        });

        descriptionField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > 1000) {
                descriptionField.setText(oldValue);
                showAlert(Alert.AlertType.WARNING, "Input Error", "Description cannot exceed 1000 characters.");
            }
        });
    }

    @FXML
    private void chooseImage(ActionEvent event) {
        String imagePath = FileDialogUtils.showImageChooser((Stage) imagePathField.getScene().getWindow());
        if (imagePath != null) {
            imagePathField.setText(imagePath);
        }
    }

    @FXML
    private void navigateToCrops(ActionEvent event) {
        if (hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to discard changes and leave?");
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    App.navigateTo("page_crops");
                }
            });
        } else {
            App.navigateTo("page_crops");
        }
    }

    @FXML
    private void saveCropChanges(ActionEvent event) {
        try {
            // Validate inputs
            String nom = cropNameField.getText().trim();
            if (nom.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Crop name is required.");
                return;
            }

            String waterNeedsText = waterNeedsField.getText().trim();
            if (waterNeedsText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs are required.");
                return;
            }
            double besoinEau;
            try {
                besoinEau = Double.parseDouble(waterNeedsText);
                if (besoinEau < 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs must be non-negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs must be a valid number.");
                return;
            }

            String nutrientNeedsText = nutrientNeedsField.getText().trim();
            if (nutrientNeedsText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs are required.");
                return;
            }
            double besoinNutriments;
            try {
                besoinNutriments = Double.parseDouble(nutrientNeedsText);
                if (besoinNutriments < 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs must be non-negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs must be a valid number.");
                return;
            }

            String description = descriptionField.getText().trim();
            String imagePath = imagePathField.getText().trim();
            if (imagePath.isEmpty()) {
                imagePath = null;
            }

            // Create Culture object and save
            Culture culture = new Culture(nom, besoinEau, besoinNutriments, imagePath, description);
            CultureService.getInstance().saveCulture(culture);

            // Show both alert and notification
            showAlert(Alert.AlertType.INFORMATION, "Success", "Crop added successfully!");
            NotificationHelper.showNotification("Crop Added", "You have successfully added " + nom + " to your crops!");

            App.navigateTo("page_crops");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save crop: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An error occurred: " + e.getMessage());
        }
    }
    private boolean hasUnsavedChanges() {
        return !cropNameField.getText().trim().isEmpty() ||
                !waterNeedsField.getText().trim().isEmpty() ||
                !nutrientNeedsField.getText().trim().isEmpty() ||
                !descriptionField.getText().trim().isEmpty() ||
                !imagePathField.getText().trim().isEmpty();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}