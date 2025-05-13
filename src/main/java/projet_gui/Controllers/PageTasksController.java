package projet_gui.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.ParcelleService;
import projet_gui.Services.TacheService;
import projet_gui.Utils.Alerts;
import projet_gui.Utils.DataSource;
import projet_gui.Utils.DataStore;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Tache;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class PageTasksController extends ControllerBaseWithSidebar {

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> priorityFilter;

    @FXML
    private ComboBox<String> fieldFilter;

    @FXML
    private TableView<Tache> tasksTable;

    @FXML
    private TableColumn<Tache, String> descriptionColumn;

    @FXML
    private TableColumn<Tache, String> statusColumn;

    @FXML
    private TableColumn<Tache, String> priorityColumn;

    @FXML
    private TableColumn<Tache, String> fieldColumn;

    @FXML
    private TableColumn<Tache, String> dueDateColumn;

    // Assignee column removed

    @FXML
    private TableColumn<Tache, Void> actionsColumn;
    
    private TacheService tacheService;
    private ParcelleService parcelleService;
    private ObservableList<Tache> tasksList;
    private SimpleDateFormat dateFormat;

    @Override
    public void initializePageContent() {
        tacheService = TacheService.getInstance();
        parcelleService = ParcelleService.getInstance();
        tasksList = FXCollections.observableArrayList();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        setupFilters();
        setupTable();
        loadTasks();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFilters() {
        // Setup status filter
        statusFilter.getItems().addAll(
            "All",
            Tache.STATUT_PENDING,
            Tache.STATUT_IN_PROGRESS,
            Tache.STATUT_DONE,
            Tache.STATUT_CANCELLED
        );
        statusFilter.setValue("All");
        
        // Setup priority filter
        priorityFilter.getItems().addAll(
            "All",
            Tache.PRIORITE_LOW,
            Tache.PRIORITE_MEDIUM,
            Tache.PRIORITE_HIGH,
            Tache.PRIORITE_URGENT
        );
        priorityFilter.setValue("All");
        
        // Setup field filter
        fieldFilter.getItems().add("All");
        try {
            List<Parcelle> parcelles = parcelleService.getAll();
            for (Parcelle parcelle : parcelles) {
                fieldFilter.getItems().add(parcelle.getId() + ": " + parcelle.getNom());
            }
            fieldFilter.setValue("All");
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading fields", e.getMessage());
        }
    }

    private void setupTable() {
        // Setup description column
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Setup status column
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));
        
        // Setup priority column
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorite()));
        
        // Setup field column
        fieldColumn.setCellValueFactory(cellData -> {
            Parcelle parcelle = cellData.getValue().getParcelle();
            return new SimpleStringProperty(parcelle != null ? parcelle.getNom() : "");
        });
        
        // Setup due date column
        dueDateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateEcheance() != null) {
                return new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateEcheance()));
            }
            return new SimpleStringProperty("");
        });
        
        // Assignee column removed
        
        // Setup actions column
        actionsColumn.setCellFactory(createActionsColumnCellFactory());
        
        // Set the items
        tasksTable.setItems(tasksList);
    }

    private Callback<TableColumn<Tache, Void>, TableCell<Tache, Void>> createActionsColumnCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Tache, Void> call(final TableColumn<Tache, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final Button completeBtn = new Button("Complete");
                    
                    {
                        editBtn.getStyleClass().add("button-small");
                        deleteBtn.getStyleClass().add("button-danger");
                        completeBtn.getStyleClass().add("button-small");
                        
                        editBtn.setOnAction(event -> {
                            Tache task = getTableView().getItems().get(getIndex());
                            editTask(task);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Tache task = getTableView().getItems().get(getIndex());
                            deleteTask(task);
                        });
                        
                        completeBtn.setOnAction(event -> {
                            Tache task = getTableView().getItems().get(getIndex());
                            markTaskComplete(task);
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Tache task = getTableView().getItems().get(getIndex());
                            HBox buttons = new HBox(5);
                            buttons.setAlignment(Pos.CENTER);
                            
                            // Only show complete button if task is not already completed
                            if (!Tache.STATUT_DONE.equals(task.getStatut())) {
                                buttons.getChildren().add(completeBtn);
                            }
                            
                            buttons.getChildren().addAll(editBtn, deleteBtn);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void loadTasks() {
        try {
            String status = statusFilter.getValue();
            String priority = priorityFilter.getValue();
            String fieldValue = fieldFilter.getValue();
            
            Integer parcelleId = null;
            if (fieldValue != null && !fieldValue.equals("All")) {
                // Extract the ID from the format "ID: Name"
                parcelleId = Integer.parseInt(fieldValue.split(":")[0].trim());
            }
            
            // Convert "All" to null for the service method
            status = "All".equals(status) ? null : status;
            priority = "All".equals(priority) ? null : priority;
            
            List<Tache> tasks = tacheService.getFiltered(status, priority, parcelleId);
            tasksList.clear();
            tasksList.addAll(tasks);
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading tasks", e.getMessage());
        }
    }

    @FXML
    private void addNewTask(ActionEvent event) {
        App.navigateTo("page_task_add");
    }

    @FXML
    private void applyFilters(ActionEvent event) {
        loadTasks();
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        statusFilter.setValue("All");
        priorityFilter.setValue("All");
        fieldFilter.setValue("All");
        loadTasks();
    }

    private void editTask(Tache task) {
        // Navigate to the edit task page and set the task ID
        DataStore.set("currentTaskId", task.getId());
        App.navigateTo("page_task_edit");
    }

    private void deleteTask(Tache task) {
        Optional<ButtonType> result = Alerts.showAlert(AlertType.CONFIRMATION, "Confirm Deletion", "Are you sure you want to delete this task?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = tacheService.delete(task.getId());
                if (deleted) {
                    tasksList.remove(task);
                    Alerts.showAlert(AlertType.INFORMATION, "Success", "Task deleted successfully");
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error", "Failed to delete task");
                }
            } catch (SQLException e) {
                Alerts.showAlert(AlertType.ERROR, "Error deleting task", e.getMessage());
            }
        }
    }

    private void markTaskComplete(Tache task) {
        try {
            boolean updated = tacheService.updateStatus(task.getId(), Tache.STATUT_DONE);
            if (updated) {
                task.setStatut(Tache.STATUT_DONE);
                tasksTable.refresh();
                Alerts.showAlert(AlertType.INFORMATION, "Success", "Task marked as complete");
            } else {
                Alerts.showAlert(AlertType.ERROR, "Error", "Failed to update task status");
            }
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error updating task", e.getMessage());
        }
    }
}