package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Entities.Tache;

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

    @FXML
    private TableColumn<Tache, String> assigneeColumn;

    @FXML
    private TableColumn<Tache, Void> actionsColumn;

    @Override
    public void initializePageContent() {
        setupFilters();
        setupTable();
        loadTasks();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFilters() {
        // TODO: Initialize filter combo boxes
        // This will be implemented by the business logic team
    }

    private void setupTable() {
        // TODO: Setup table columns and cell factories
        // This will be implemented by the business logic team
    }

    private void loadTasks() {
        // TODO: Load tasks based on current filters
        // This will be implemented by the business logic team
    }

    @FXML
    private void addNewTask(ActionEvent event) {
        // TODO: Implement new task creation
        App.navigateTo("page_task_add");
    }

    @FXML
    private void applyFilters(ActionEvent event) {
        loadTasks();
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        statusFilter.setValue(null);
        priorityFilter.setValue(null);
        fieldFilter.setValue(null);
        loadTasks();
    }

    private void editTask(Tache task) {
        // TODO: Implement task editing
    }

    private void deleteTask(Tache task) {
        // TODO: Implement task deletion with confirmation
    }

    private void markTaskComplete(Tache task) {
        // TODO: Implement task completion
    }
}