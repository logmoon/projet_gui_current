package projet_gui.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import projet_gui.App;
import projet_gui.Entities.Utilisateur;
import projet_gui.Services.AuthService;
import projet_gui.Services.UtilisateurService;
import projet_gui.Utils.DataStore;

import java.sql.SQLException;
import java.util.Optional;

public class AdminUserDetailsController extends ControllerBaseWithSidebar {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<?> fieldsTable;

    @FXML
    private TableColumn<?, Integer> fieldIdColumn;

    @FXML
    private TableColumn<?, String> fieldNameColumn;

    @FXML
    private TableColumn<?, String> fieldLocationColumn;

    @FXML
    private TableColumn<?, Double> fieldSizeColumn;

    @FXML
    private TableView<?> cropsTable;

    @FXML
    private TableColumn<?, Integer> cropIdColumn;

    @FXML
    private TableColumn<?, String> cropNameColumn;

    @FXML
    private TableColumn<?, String> cropFieldColumn;

    @FXML
    private TableColumn<?, String> cropStatusColumn;

    private UtilisateurService utilisateurService;
    private Utilisateur selectedUser;

    @Override
    public boolean canEnter() {
        AuthService authService = AuthService.getInstance();
        return authService.isAuthenticated() && 
               authService.getCurrentToken() != null && 
               authService.getCurrentToken().getUser().isAdmin();
    }

    @Override
    protected void initializePageContent() {
        utilisateurService = UtilisateurService.getInstance();
        
        // Get the selected user ID from DataStore
        Integer selectedUserId = DataStore.get("selectedUserId", Integer.class);
        if (selectedUserId == null) {
            // If no user ID is found, go back to the dashboard
            App.navigateTo("admin_dashboard");
            return;
        }
        
        loadUserDetails(selectedUserId);
        setupTables();
        loadUserData();
    }

    private void loadUserDetails(int userId) {
        try {
            Optional<Utilisateur> user = utilisateurService.getUserById(userId);
            if (user.isPresent()) {
                selectedUser = user.get();
                nameLabel.setText(selectedUser.getPrenom() + " " + selectedUser.getNom());
                emailLabel.setText(selectedUser.getEmail());
                statusLabel.setText(selectedUser.getActif() ? "Active" : "Inactive");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTables() {
        // Setup fields table
        fieldIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fieldNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        fieldLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        fieldSizeColumn.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        
        // Setup crops table
        cropIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cropNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        cropFieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        cropStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadUserData() {
        if (selectedUser == null) return;
        
        // TODO: Load fields and crops for the selected user
        // This will be implemented when the corresponding services are available
        // For now, we'll just show empty tables
        fieldsTable.setItems(FXCollections.observableArrayList());
        cropsTable.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void goBack() {
        App.navigateTo("admin_dashboard");
    }
}