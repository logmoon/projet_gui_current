package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Utils.FileDialogUtils;

import java.sql.SQLException;

public class PageCropEditController extends ControllerBaseWithSidebar {

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

    private Culture currentCulture;

    public interface UpdateCallback {
        void onUpdate(Culture updatedCulture);
    }

    private UpdateCallback updateCallback;

    // Add this setter method
    public void setOnUpdateCallback(UpdateCallback callback) {
        this.updateCallback = callback;
    }
    @Override
    public void initializePageContent() {
        loadCropData();
        setupFormFields();
    }

    @Override
    public boolean canEnter() {
        boolean isAuthenticated = AuthService.getInstance().isAuthenticated();
        System.out.println("PageCropEditController - User authenticated: " + isAuthenticated);
        return isAuthenticated;
    }

    private void loadCropData() {
        Integer cropId = (Integer) App.getNavigationParam();
        if (cropId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No crop ID provided.");
            App.navigateTo("page_crops");
            return;
        }

        try {
            currentCulture = CultureService.getInstance().getCultureById(cropId);
            if (currentCulture == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Crop not found.");
                App.navigateTo("page_crops");
                return;
            }

            // Pré-remplir les champs
            cropNameField.setText(currentCulture.getNom());
            waterNeedsField.setText(String.valueOf(currentCulture.getBesoinEau()));
            nutrientNeedsField.setText(String.valueOf(currentCulture.getBesoinNutriments()));
            descriptionField.setText(currentCulture.getDescription() != null ? currentCulture.getDescription() : "");
            imagePathField.setText(currentCulture.getImagePath() != null ? currentCulture.getImagePath() : "");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load crop: " + e.getMessage());
            App.navigateTo("page_crops");
        }
    }

    private void setupFormFields() {
        // Validation basique
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
                if (response == ButtonType.OK) {
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
            // Validate crop name
            String nom = cropNameField.getText().trim();
            if (nom.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Crop name is required.");
                cropNameField.requestFocus();
                return;
            }
            if (nom.length() > 100) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Crop name cannot exceed 100 characters.");
                cropNameField.requestFocus();
                return;
            }

            // Validate water needs
            String waterNeedsText = waterNeedsField.getText().trim();
            if (waterNeedsText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs are required.");
                waterNeedsField.requestFocus();
                return;
            }
            double besoinEau;
            try {
                besoinEau = Double.parseDouble(waterNeedsText);
                if (besoinEau < 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs must be non-negative.");
                    waterNeedsField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Water needs must be a valid number.");
                waterNeedsField.requestFocus();
                return;
            }

            // Validate nutrient needs
            String nutrientNeedsText = nutrientNeedsField.getText().trim();
            if (nutrientNeedsText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs are required.");
                nutrientNeedsField.requestFocus();
                return;
            }
            double besoinNutriments;
            try {
                besoinNutriments = Double.parseDouble(nutrientNeedsText);
                if (besoinNutriments < 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs must be non-negative.");
                    nutrientNeedsField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Nutrient needs must be a valid number.");
                nutrientNeedsField.requestFocus();
                return;
            }

            // Validate description length
            String description = descriptionField.getText().trim();
            if (description.length() > 1000) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Description cannot exceed 1000 characters.");
                descriptionField.requestFocus();
                return;
            }

            // Handle image path
            String imagePath = imagePathField.getText().trim();
            if (imagePath.isEmpty()) {
                imagePath = null;
            }

            // Update the culture object
            currentCulture.setNom(nom);
            currentCulture.setBesoinEau(besoinEau);
            currentCulture.setBesoinNutriments(besoinNutriments);
            currentCulture.setDescription(description.isEmpty() ? null : description);
            currentCulture.setImagePath(imagePath);

            // Save changes
            Culture updatedCulture = CultureService.getInstance().updateCulture(currentCulture);

            // Notify callback if exists
            if (updateCallback != null) {
                updateCallback.onUpdate(updatedCulture);
            }

            // Show success and navigate back
            showAlert(Alert.AlertType.INFORMATION, "Success", "Crop updated successfully!");
            App.navigateTo("page_crops");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to update crop in database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                    "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean hasUnsavedChanges() {
        if (currentCulture == null) return false;

        return !cropNameField.getText().trim().equals(currentCulture.getNom()) ||
                !waterNeedsField.getText().trim().equals(String.valueOf(currentCulture.getBesoinEau())) ||
                !nutrientNeedsField.getText().trim().equals(String.valueOf(currentCulture.getBesoinNutriments())) ||
                !descriptionField.getText().trim().equals(currentCulture.getDescription() != null ? currentCulture.getDescription() : "") ||
                !imagePathField.getText().trim().equals(currentCulture.getImagePath() != null ? currentCulture.getImagePath() : "");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}