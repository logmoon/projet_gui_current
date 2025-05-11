package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Entities.Parcelle;

public class PageFieldsController extends ControllerBaseWithSidebar {

    @FXML
    private TableView<Parcelle> fieldsTable;

    @FXML
    private TableColumn<Parcelle, String> nameColumn;

    @FXML
    private TableColumn<Parcelle, Double> areaColumn;

    @FXML
    private TableColumn<Parcelle, String> locationColumn;

    @FXML
    private TableColumn<Parcelle, String> cropsColumn;

    @FXML
    private TableColumn<Parcelle, Void> actionsColumn;

    @Override
    public void initializePageContent() {
        // Initialize table columns
        setupTableColumns();
        // Load field data
        loadFieldsData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupTableColumns() {
        // TODO: Setup cell value factories and cell factories
        // This will be implemented by the business logic team
    }

    private void loadFieldsData() {
        // TODO: Load actual field data from services
        // This will be implemented by the business logic team
    }

    @FXML
    private void addNewField(ActionEvent event) {
        // TODO: Implement new field creation
        App.navigateTo("page_field_add");
    }

    @FXML
    private void viewFieldDetails(Parcelle field) {
        // TODO: Implement field details view
        App.navigateTo("page_field_detail");
    }

    @FXML
    private void deleteField(Parcelle field) {
        // TODO: Implement field deletion with confirmation
    }
}