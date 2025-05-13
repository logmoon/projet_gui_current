package projet_gui.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import projet_gui.App;
import projet_gui.Entities.Role;
import projet_gui.Entities.Utilisateur;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.Alerts;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController extends ControllerBaseWithSidebar {

    @FXML
    private TableView<Utilisateur> usersTable;

    @FXML
    private TableColumn<Utilisateur, String> nameColumn;

    @FXML
    private TableColumn<Utilisateur, String> emailColumn;

    @FXML
    private TableColumn<Utilisateur, Boolean> statusColumn;

    @FXML
    private TableColumn<Utilisateur, Void> actionsColumn;

    private UtilisateurService utilisateurService;

    @Override
    public boolean canEnter() {
        AuthService authService = AuthService.getInstance();
        return authService.isAuthenticated() && 
               authService.getCurrentToken().getUser().isAdmin();
    }

    @Override
    protected void initializePageContent() {
        utilisateurService = UtilisateurService.getInstance();
        setupTable();
        loadUsers();
    }

    private void setupTable() {
        // Configure table columns
        nameColumn.setCellValueFactory(cellData -> {
            Utilisateur user = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> user.getPrenom() + " " + user.getNom());
        });
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("actif"));
        statusColumn.setCellFactory(column -> new TableCell<Utilisateur, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Active" : "Inactive");
                }
            }
        });

        // Setup actions column
        setupActionsColumn();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Utilisateur, Void>() {
            private final Button viewButton = new Button("View");
            private final Button makeAdminButton = new Button("Make Admin");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(6, viewButton, makeAdminButton, deleteButton);

            {
                viewButton.getStyleClass().add("button-primary");
                viewButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    viewUser(user);
                });

                makeAdminButton.getStyleClass().add("button-primary");
                makeAdminButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    makeUserAdmin(user);
                });

                deleteButton.getStyleClass().add("button-accent");
                deleteButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            // Get all users
            List<Utilisateur> allUsers = utilisateurService.getAllUsers();
            
            // Filter out admin users
            List<Utilisateur> regularUsers = allUsers.stream()
                .filter(user -> user.getRole() != Role.ADMIN)
                .collect(Collectors.toList());
            
            ObservableList<Utilisateur> users = FXCollections.observableArrayList(regularUsers);
            usersTable.setItems(users);
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void viewUser(Utilisateur user) {
        // Store the selected user ID in the DataStore
        // so it can be accessed by the user details controller
        projet_gui.Utils.DataStore.set("selectedUserId", user.getId());
        App.navigateTo("admin_user_details");
    }

    private void deleteUser(Utilisateur user) {
        Optional<ButtonType> result = Alerts.showAlert(
            AlertType.CONFIRMATION, 
            "Delete User", 
            "Are you sure you want to delete user: " + user.getPrenom() + " " + user.getNom() + "?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                utilisateurService.deleteUser(user.getId());
                loadUsers(); // Refresh the table
            } catch (SQLException e) {
                Alerts.showAlert(AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    private void makeUserAdmin(Utilisateur user) {
        if (user.getRole() == Role.ADMIN) {
            Alerts.showAlert(AlertType.INFORMATION, "Info", "User is already an admin.");
            return;
        }

        Optional<ButtonType> result = Alerts.showAlert(
            AlertType.CONFIRMATION, 
            "Make User Admin", 
            "Are you sure you want to turn user: " + user.getPrenom() + " " + user.getNom() + " into an admin?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                utilisateurService.makeAdmin(user.getId());
                loadUsers(); // Refresh the table
            } catch (SQLException e) {
                Alerts.showAlert(AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }
}