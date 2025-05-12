package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import projet_gui.Services.AuthService;
import projet_gui.Entities.Maladie;

public class PageDiseasesController extends ControllerBaseWithSidebar {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Maladie> diseasesTable;

    @FXML
    private TableColumn<Maladie, String> nameColumn;

    @FXML
    private TableColumn<Maladie, String> descriptionColumn;

    @FXML
    private TableColumn<Maladie, String> symptomsColumn;

    @FXML
    private TableColumn<Maladie, String> treatmentColumn;

    @FXML
    private TableColumn<Maladie, Void> actionsColumn;

    @FXML
    private VBox diseaseDetailsContainer;

    @Override
    public void initializePageContent() {
        setupTable();
        loadDiseases();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupTable() {
        // TODO: Setup table columns and cell factories
        // This will be implemented by the business logic team
    }

    private void loadDiseases() {
        // TODO: Load disease data
        // This will be implemented by the business logic team
    }

    @FXML
    private void searchDiseases(ActionEvent event) {
        String searchTerm = searchField.getText().trim();
        // TODO: Implement disease search
        // This will be implemented by the business logic team
    }

    @FXML
    private void addNewDisease(ActionEvent event) {
        // TODO: Implement new disease creation
    }

    private void showDiseaseDetails(Maladie disease) {
        // TODO: Show detailed disease information
        diseaseDetailsContainer.setVisible(true);
    }

    private void editDisease(Maladie disease) {
        // TODO: Implement disease editing
    }

    private void deleteDisease(Maladie disease) {
        // TODO: Implement disease deletion with confirmation
    }
}