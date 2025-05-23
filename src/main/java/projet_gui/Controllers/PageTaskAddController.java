package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Tache;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Services.ParcelleService;
import projet_gui.Services.TacheService;
import projet_gui.Utils.Alerts;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PageTaskAddController extends ControllerBaseWithSidebar {

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private ComboBox<String> fieldComboBox;
    
    @FXML
    private ComboBox<String> cultureComboBox;

    @FXML
    private TextField dueDateField;
    
    private TacheService tacheService;
    private ParcelleService parcelleService;
    private CultureService cultureService;
    private List<Parcelle> parcelles;
    private SimpleDateFormat dateFormat;

    @Override
    public void initializePageContent() {
        tacheService = TacheService.getInstance();
        parcelleService = ParcelleService.getInstance();
        cultureService = CultureService.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Initialize form fields
        setupFormFields();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFormFields() {
        // Setup priority combo box
        priorityComboBox.getItems().addAll(
            Tache.PRIORITE_LOW,
            Tache.PRIORITE_MEDIUM,
            Tache.PRIORITE_HIGH,
            Tache.PRIORITE_URGENT
        );
        priorityComboBox.setValue(Tache.PRIORITE_MEDIUM);
        
        // Setup field combo box
        try {
            parcelles = parcelleService.getAll();
            if (parcelles.isEmpty()) {
                Alerts.showAlert(AlertType.WARNING, "No Fields Available", "You need to create at least one field before creating tasks.");
                fieldComboBox.setDisable(true);
                cultureComboBox.setDisable(true);
            } else {
                for (Parcelle parcelle : parcelles) {
                    fieldComboBox.getItems().add(parcelle.getId() + ": " + parcelle.getNom());
                }
                fieldComboBox.setValue(fieldComboBox.getItems().get(0));
                
                // Load cultures for the selected field
                loadCulturesForSelectedField();
            }
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading fields", e.getMessage());
        }
        
        // Set default due date (tomorrow)
        Date tomorrow = new Date(System.currentTimeMillis() + 86400000); // 24 hours in milliseconds
        dueDateField.setText(dateFormat.format(tomorrow));
    }

    @FXML
    private void navigateToTasks(ActionEvent event) {
        App.navigateTo("page_tasks");
    }
    
    @FXML
    private void onFieldSelected(ActionEvent event) {
        loadCulturesForSelectedField();
    }
    
    private void loadCulturesForSelectedField() {
        if (fieldComboBox.getValue() == null) {
            return;
        }
        
        try {
            // Clear previous items
            cultureComboBox.getItems().clear();
            
            // Add a "None" option
            cultureComboBox.getItems().add("None");
            
            // Get the selected parcelle ID
            String fieldValue = fieldComboBox.getValue();
            int parcelleId = Integer.parseInt(fieldValue.split(":")[0].trim());
            
            // Load cultures for this field
            List<Culture> cultures = cultureService.getCulturesByParcelleId(parcelleId);
            
            if (cultures.isEmpty()) {
                cultureComboBox.setValue("None");
            } else {
                for (Culture culture : cultures) {
                    cultureComboBox.getItems().add(culture.getId() + ": " + culture.getNom());
                }
                cultureComboBox.setValue(cultureComboBox.getItems().get(0));
            }
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading cultures", e.getMessage());
        }
    }

    @FXML
    private void saveTaskChanges(ActionEvent event) {
        if (validateForm()) {
            try {
                Tache newTask = createTaskFromForm();
                Tache savedTask = tacheService.create(newTask);
                
                if (savedTask != null) {
                    Alerts.showAlert(AlertType.INFORMATION, "Success", "Task created successfully");
                    App.navigateTo("page_tasks");
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error", "Failed to create task");
                }
            } catch (SQLException | ParseException e) {
                Alerts.showAlert(AlertType.ERROR, "Error creating task", e.getMessage());
            }
        }
    }
    
    private boolean validateForm() {
        // Check if description is empty
        if (descriptionField.getText().trim().isEmpty()) {
            Alerts.showAlert(AlertType.ERROR, "Validation Error", "Description cannot be empty");
            return false;
        }
        
        // Check if field is selected
        if (fieldComboBox.getValue() == null || fieldComboBox.getItems().isEmpty()) {
            Alerts.showAlert(AlertType.ERROR, "Validation Error", "You must select a field");
            return false;
        }
        
        // Check if due date is valid
        try {
            dateFormat.parse(dueDateField.getText());
        } catch (ParseException e) {
            Alerts.showAlert(AlertType.ERROR, "Validation Error", "Due date must be in format yyyy-MM-dd HH:mm");
            return false;
        }
        
        return true;
    }
    
    private Tache createTaskFromForm() throws ParseException {
        Tache task = new Tache();
        
        // Set description
        task.setDescription(descriptionField.getText().trim());
        
        // Set priority
        task.setPriorite(priorityComboBox.getValue());
        
        // Set status (default to PENDING)
        task.setStatut(Tache.STATUT_PENDING);
        
        // Set due date
        Date dueDate = dateFormat.parse(dueDateField.getText());
        task.setDateEcheance(new Timestamp(dueDate.getTime()));
        
        // Set parcelle
        String fieldValue = fieldComboBox.getValue();
        int parcelleId = Integer.parseInt(fieldValue.split(":")[0].trim());
        
        for (Parcelle parcelle : parcelles) {
            if (parcelle.getId() == parcelleId) {
                task.setParcelle(parcelle);
                break;
            }
        }
        
        // Set culture if selected
        String cultureValue = cultureComboBox.getValue();
        if (cultureValue != null && !cultureValue.equals("None")) {
            try {
                int cultureId = Integer.parseInt(cultureValue.split(":")[0].trim());
                Culture culture = cultureService.getCultureById(cultureId);
                if (culture != null) {
                    task.setCulture(culture);
                }
            } catch (SQLException | NumberFormatException e) {
                // If there's an error, we'll just not set the culture
                System.out.println("Error setting culture: " + e.getMessage());
            }
        }
        
        return task;
    }
}