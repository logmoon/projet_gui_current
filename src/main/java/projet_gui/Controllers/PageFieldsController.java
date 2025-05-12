package projet_gui.Controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Services.ParcelleService;
import projet_gui.Utils.Alerts;
import projet_gui.Utils.DataStore;
import projet_gui.Entities.Culture;
import projet_gui.Entities.Parcelle;
import projet_gui.Controllers.PageFieldDetailController;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    
    private ParcelleService parcelleService;
    private CultureService cultureService;

    @Override
    public void initializePageContent() {
        parcelleService = ParcelleService.getInstance();
        cultureService = CultureService.getInstance();
        
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
        // Setup cell value factories
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        
        // Area column (length * width)
        areaColumn.setCellValueFactory(cellData -> {
            Parcelle parcelle = cellData.getValue();
            double area = parcelle.getLongueur() * parcelle.getLargeur();
            return new SimpleDoubleProperty(area).asObject();
        });
        
        // Format area with 2 decimal places
        areaColumn.setCellFactory(column -> new TableCell<Parcelle, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f m²", item));
                }
            }
        });
        
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocalisationCity()));
        
        // Active crops column
        cropsColumn.setCellValueFactory(cellData -> {
            Parcelle parcelle = cellData.getValue();
            try {
                List<Culture> activeCrops = cultureService.getCulturesByParcelleId(parcelle.getId());
                if (activeCrops.isEmpty()) {
                    return new SimpleStringProperty("No crops");
                }
                else {
                    StringBuilder crops = new StringBuilder();
                    for (Culture crop : activeCrops) {
                        crops.append(crop.getNom()).append(", ");
                    }
                    return new SimpleStringProperty(crops.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Error loading crops");
            }
        });
        
        // Actions column with buttons
        actionsColumn.setCellFactory(createActionsColumnCellFactory());
    }
    
    private Callback<TableColumn<Parcelle, Void>, TableCell<Parcelle, Void>> createActionsColumnCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Parcelle, Void> call(final TableColumn<Parcelle, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox pane = new HBox(5, viewBtn, deleteBtn);
                    
                    {
                        viewBtn.getStyleClass().add("button-small");
                        deleteBtn.getStyleClass().add("button-small");
                        deleteBtn.getStyleClass().add("button-danger");
                        pane.setAlignment(Pos.CENTER);
                        
                        viewBtn.setOnAction(event -> {
                            Parcelle parcelle = getTableView().getItems().get(getIndex());
                            viewFieldDetails(parcelle);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Parcelle parcelle = getTableView().getItems().get(getIndex());
                            deleteField(parcelle);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
    }

    private void loadFieldsData() {
        try {
            // Get the current user's ID from the auth token
            int userId = AuthService.getInstance().getCurrentToken().getUser().getId();
            
            // Load parcelles for the current user
            List<Parcelle> parcelles = parcelleService.getAllByUserId(userId);
            fieldsTable.setItems(FXCollections.observableArrayList(parcelles));
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void addNewField(ActionEvent event) {
        App.navigateTo("page_field_add");
    }

    @FXML
    private void viewFieldDetails(Parcelle field) {
        // Store the selected field
        DataStore.set("selectedParcelleId", field.getId());
        App.navigateTo("page_field_detail");
    }

    @FXML
    private void deleteField(Parcelle field) {
        Optional<ButtonType> result = Alerts.showAlert(AlertType.CONFIRMATION, "Confirm Deletion", "Are you sure you want to delete the field '" + field.getNom() + "'? This action cannot be undone.");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = parcelleService.delete(field.getId());
                if (deleted) {
                    // Refresh the table
                    loadFieldsData();
                    Alerts.showAlert(AlertType.INFORMATION, "Success", "The field has been successfully deleted.");
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error", "Failed to delete the field.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alerts.showAlert(AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }
}