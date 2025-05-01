package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import projet_gui.App;
import projet_gui.Services.AuthService;

public class PageTaskAddController extends ControllerBaseWithSidebar {

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private ComboBox<String> fieldComboBox;

    @FXML
    private TextField dueDateField;

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
    private void navigateToTasks(ActionEvent event) {
        App.navigateTo("page_tasks");
    }

    @FXML
    private void saveTaskChanges(ActionEvent event) {
        // TODO: Implement task saving logic
        // This will be implemented by the business logic team
    }
}