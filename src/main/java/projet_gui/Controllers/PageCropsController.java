package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Entities.Culture;

public class PageCropsController extends ControllerBaseWithSidebar {

    @FXML
    private TableView<Culture> cropsTable;

    @FXML
    private TableColumn<Culture, String> nameColumn;

    @FXML
    private TableColumn<Culture, Double> waterNeedsColumn;

    @FXML
    private TableColumn<Culture, Double> nutrientNeedsColumn;

    @FXML
    private TableColumn<Culture, String> descriptionColumn;

    @FXML
    private TableColumn<Culture, Void> actionsColumn;

    @Override
    public void initializePageContent() {
        // Initialize table columns
        setupTableColumns();
        // Load crop data
        loadCropsData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupTableColumns() {
        // TODO: Setup cell value factories and cell factories
        // This will be implemented by the business logic team
    }

    private void loadCropsData() {
        // TODO: Load crop data from services
        // This will be implemented by the business logic team
    }

    @FXML
    private void addNewCrop(ActionEvent event) {
        App.navigateTo("page_crop_add");
    }
}