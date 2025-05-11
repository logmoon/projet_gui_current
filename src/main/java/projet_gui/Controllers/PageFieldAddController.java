package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.ParcelleService;
import projet_gui.Utils.FileDialogUtils;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Utilisateur;

import java.sql.SQLException;

public class PageFieldAddController extends ControllerBaseWithSidebar {

    @FXML
    private TextField fieldNameField;

    @FXML
    private TextField longueurField;

    @FXML
    private TextField largeurField;

    @FXML
    private TextField locationComboBox;

    @FXML
    private TextField imagePathField;
    
    private ParcelleService parcelleService;

    @Override
    public void initializePageContent() {
        parcelleService = ParcelleService.getInstance();
        // Clear form fields
        clearForm();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }
    
    private void clearForm() {
        fieldNameField.clear();
        longueurField.clear();
        largeurField.clear();
        locationComboBox.clear();
        imagePathField.clear();
    }
    
    @FXML
    private void chooseImage(ActionEvent event) {
        String imagePath = FileDialogUtils.showImageChooser((Stage)imagePathField.getScene().getWindow());
        if (imagePath != null) {
            imagePathField.setText(imagePath);
        }
    }

    @FXML
    private void navigateToFields(ActionEvent event) {
        App.navigateTo("page_fields");
    }

    @FXML
    private void saveFieldChanges(ActionEvent event) {
        try {
            // Validate input fields
            if (fieldNameField.getText().trim().isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "Field name is required", "Please enter a name for the field.");
                return;
            }
            
            if (locationComboBox.getText().trim().isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "Location is required", "Please enter a location for the field.");
                return;
            }
            
            // Validate numeric fields
            double longueur, largeur;
            try {
                longueur = Double.parseDouble(longueurField.getText());
                if (longueur <= 0) {
                    showAlert(AlertType.ERROR, "Validation Error", "Invalid length", "Length must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid length", "Please enter a valid number for length.");
                return;
            }
            
            try {
                largeur = Double.parseDouble(largeurField.getText());
                if (largeur <= 0) {
                    showAlert(AlertType.ERROR, "Validation Error", "Invalid width", "Width must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid width", "Please enter a valid number for width.");
                return;
            }
            
            // Get current user
            Utilisateur currentUser = AuthService.getInstance().getCurrentToken().getUser();
            
            // Create parcelle object
            Parcelle parcelle = new Parcelle(
                fieldNameField.getText(),
                longueur,
                largeur,
                locationComboBox.getText(),
                currentUser,
                imagePathField.getText()
            );
            
            // Save to database
            parcelleService.create(parcelle);
            
            // Show success message
            showAlert(AlertType.INFORMATION, "Success", "Field Added", "The field has been successfully added.");
            
            // Navigate back to fields list
            App.navigateTo("page_fields");
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to save field", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred", e.getMessage());
        }
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}