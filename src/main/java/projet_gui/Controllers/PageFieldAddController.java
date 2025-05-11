package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Utils.FileDialogUtils;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Localisation;
import projet_gui.Entities.Utilisateur;

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

    @Override
    public void initializePageContent() {
        setupComboBoxes();
        setupTables();
        loadFieldData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupComboBoxes() {
        // TODO: Initialize combo boxes with valid options
        // This will be implemented by the business logic team
    }

    private void setupTables() {
        // TODO: Setup table columns and cell factories
        // This will be implemented by the business logic team
    }

    private void loadFieldData() {
        // TODO: Load field data and populate form
        // This will be implemented by the business logic team
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
            Parcelle parcelle = new Parcelle(
                fieldNameField.getText(),
                Double.parseDouble(longueurField.getText()),
                Double.parseDouble(largeurField.getText()),
                locationComboBox.getText(),
                AuthService.getInstance().getCurrentToken().getUser(),
                imagePathField.getText()
            );
            // TODO: Save parcelle to database
        } catch (Exception e) {
            // TODO: Show error message
            e.printStackTrace();
        }
    }
}