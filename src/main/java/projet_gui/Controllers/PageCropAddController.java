package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Utils.FileDialogUtils;

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
        // Initialize form fields
        setupFormFields();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFormFields() {
        // TODO: Initialize form fields with default values
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
    private void navigateToCrops(ActionEvent event) {
        App.navigateTo("page_crops");
    }

    @FXML
    private void saveCropChanges(ActionEvent event) {
        // TODO: Implement crop saving logic
        // This will be implemented by the business logic team
    }
}