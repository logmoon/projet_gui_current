package projet_gui.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;

import java.sql.SQLException;

public class PageCropsController extends ControllerBaseWithSidebar {

    @FXML
    private TableView<Culture> cropsTable;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Button clearFiltersButton;
    @FXML
    private Pagination paginationControl;

    private ObservableList<Culture> cropList = FXCollections.observableArrayList();
    private FilteredList<Culture> filteredCrops;
    private static final int ITEMS_PER_PAGE = 10;

    @Override
    public void initializePageContent() {
        setupTableColumns();
        setupFilters();
        loadCropsData();
        setupPagination();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupTableColumns() {
        // Set up action buttons column
        TableColumn<Culture, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button qrButton = new Button("QR Code");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(qrButton, deleteButton);

            {
                pane.setSpacing(5);
                qrButton.getStyleClass().add("button-small");
                deleteButton.getStyleClass().add("button-small-danger");

                qrButton.setOnAction(event -> {
                    Culture culture = getTableView().getItems().get(getIndex());
                    showQRCode(culture);
                });

                deleteButton.setOnAction(event -> {
                    Culture culture = getTableView().getItems().get(getIndex());
                    deleteCrop(culture);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        cropsTable.getColumns().add(actionsCol);
    }

    private void setupFilters() {
        // Initialize filter options
        filterComboBox.getItems().addAll(
                "All Crops",
                "High Water Needs (>400mm)",
                "Medium Water Needs (200-400mm)",
                "Low Water Needs (<200mm)",
                "High Nutrient Needs (>80kg/ha)",
                "Medium Nutrient Needs (30-80kg/ha)",
                "Low Nutrient Needs (<30kg/ha)"
        );
        filterComboBox.setValue("All Crops");

        // Set up search and filter functionality
        filteredCrops = new FilteredList<>(cropList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCrops();
        });

        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterCrops();
        });

        clearFiltersButton.setOnAction(e -> {
            searchField.clear();
            filterComboBox.setValue("All Crops");
            filterCrops();
        });
    }

    private void setupPagination() {
        paginationControl.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            updateTableContent();
        });

        filteredCrops.addListener((javafx.collections.ListChangeListener.Change<? extends Culture> change) -> {
            updatePagination();
        });
    }

    private void updatePagination() {
        int itemCount = filteredCrops.size();
        int pageCount = (int) Math.ceil((double) itemCount / ITEMS_PER_PAGE);
        paginationControl.setPageCount(pageCount == 0 ? 1 : pageCount);
        updateTableContent();
    }

    private void filterCrops() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        filteredCrops.setPredicate(crop -> {
            // Search filter
            if (!searchText.isEmpty() &&
                    !crop.getNom().toLowerCase().contains(searchText) &&
                    !crop.getDescription().toLowerCase().contains(searchText)) {
                return false;
            }

            // Water needs filter
            switch (filterValue) {
                case "High Water Needs (>400mm)":
                    return crop.getBesoinEau() > 400;
                case "Medium Water Needs (200-400mm)":
                    return crop.getBesoinEau() >= 200 && crop.getBesoinEau() <= 400;
                case "Low Water Needs (<200mm)":
                    return crop.getBesoinEau() < 200;
                case "High Nutrient Needs (>80kg/ha)":
                    return crop.getBesoinNutriments() > 80;
                case "Medium Nutrient Needs (30-80kg/ha)":
                    return crop.getBesoinNutriments() >= 30 && crop.getBesoinNutriments() <= 80;
                case "Low Nutrient Needs (<30kg/ha)":
                    return crop.getBesoinNutriments() < 30;
                default: // "All Crops"
                    return true;
            }
        });

        updatePagination();
    }

    private void loadCropsData() {
        try {
            cropList.setAll(CultureService.getInstance().getAllCultures());
            filterCrops(); // Apply initial filtering
        } catch (SQLException e) {
            System.err.println("Failed to load crops: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load crops: " + e.getMessage());
            // Fallback to mock data
            cropList.setAll(FXCollections.observableArrayList(
                    new Culture("Wheat", 500.0, 100.0, "images/wheat.jpg", "A cereal grain"),
                    new Culture("Tomato", 300.0, 50.0, "images/tomato.jpg", "A fruit vegetable"),
                    new Culture("Rice", 600.0, 120.0, "images/rice.jpg", "Staple food crop"),
                    new Culture("Corn", 350.0, 70.0, "images/corn.jpg", "Versatile grain crop"),
                    new Culture("Potato", 250.0, 40.0, "images/potato.jpg", "Root vegetable"),
                    new Culture("Lettuce", 150.0, 20.0, "images/lettuce.jpg", "Leafy green vegetable")
            ));
            filterCrops(); // Apply initial filtering
        }
    }

    private void updateTableContent() {
        int pageIndex = paginationControl.getCurrentPageIndex();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredCrops.size());

        if (fromIndex >= filteredCrops.size()) {
            // Handle case where page index is beyond available items
            fromIndex = 0;
            paginationControl.setCurrentPageIndex(0);
        }

        ObservableList<Culture> pageItems = FXCollections.observableArrayList(
                filteredCrops.subList(fromIndex, toIndex)
        );
        cropsTable.setItems(pageItems);

        // Show placeholder if no crops match the filters
        if (filteredCrops.isEmpty()) {
            cropsTable.setPlaceholder(new Label("No crops match your search/filters."));
        }
    }

    private void showQRCode(Culture culture) {
        try {
            // Create and display QR code in a new window
            QrCodeController qrController = new QrCodeController(culture);
            Stage qrStage = new Stage();
            qrController.buildUI();
            qrController.start(qrStage);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "QR Code Error", "Failed to generate QR code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCrop(Culture culture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Crop");
        alert.setContentText("Are you sure you want to delete " + culture.getNom() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    CultureService.getInstance().deleteCulture(culture.getId());
                    cropList.remove(culture);
                    filterCrops(); // Refresh the display with updated filters
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Crop deleted successfully.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete crop: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void addNewCrop(ActionEvent event) {
        App.navigateTo("page_crop_add");
    }
    @FXML
    private void UpdateCrop(ActionEvent event) {
        App.navigateTo("page_crop_edit");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}