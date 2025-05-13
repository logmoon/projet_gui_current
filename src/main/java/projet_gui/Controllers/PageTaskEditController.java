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
import projet_gui.Utils.DataStore;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PageTaskEditController extends ControllerBaseWithSidebar {

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> statusComboBox;
    
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
    private Tache currentTask;
    private Integer currentTaskId;

    @Override
    public void initializePageContent() {
        tacheService = TacheService.getInstance();
        parcelleService = ParcelleService.getInstance();
        cultureService = CultureService.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Get task ID from parameters
        currentTaskId = DataStore.get("currentTaskId", Integer.class);
        if (currentTaskId == null) {
            Alerts.showAlert(AlertType.ERROR, "Error", "No task ID provided");
            App.navigateTo("page_tasks");
            return;
        }
        
        // Initialize form fields
        setupFormFields();
        
        // Load task data
        loadTaskData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }

    private void setupFormFields() {
        // Setup status combo box
        statusComboBox.getItems().addAll(
            Tache.STATUT_PENDING,
            Tache.STATUT_IN_PROGRESS,
            Tache.STATUT_DONE,
            Tache.STATUT_CANCELLED
        );
        
        // Setup priority combo box
        priorityComboBox.getItems().addAll(
            Tache.PRIORITE_LOW,
            Tache.PRIORITE_MEDIUM,
            Tache.PRIORITE_HIGH,
            Tache.PRIORITE_URGENT
        );
        
        // Setup field combo box
        try {
            parcelles = parcelleService.getAll();
            if (parcelles.isEmpty()) {
                fieldComboBox.setDisable(true);
                cultureComboBox.setDisable(true);
            } else {
                for (Parcelle parcelle : parcelles) {
                    fieldComboBox.getItems().add(parcelle.getId() + ": " + parcelle.getNom());
                }
            }
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading fields", e.getMessage());
        }
    }
    
    private void loadTaskData() {
        try {
            currentTask = tacheService.getById(currentTaskId);
            if (currentTask == null) {
                Alerts.showAlert(AlertType.ERROR, "Error", "Task not found");
                App.navigateTo("page_tasks");
                return;
            }
            
            // Set description
            descriptionField.setText(currentTask.getDescription());
            
            // Set status
            statusComboBox.setValue(currentTask.getStatut());
            
            // Set priority
            priorityComboBox.setValue(currentTask.getPriorite());
            
            // Set field
            Parcelle parcelle = currentTask.getParcelle();
            if (parcelle != null) {
                String fieldValue = parcelle.getId() + ": " + parcelle.getNom();
                fieldComboBox.setValue(fieldValue);
                
                // Load cultures for this field
                loadCulturesForSelectedField();
                
                // Set culture if exists
                if (currentTask.getCulture() != null) {
                    Culture culture = currentTask.getCulture();
                    String cultureValue = culture.getId() + ": " + culture.getNom();
                    
                    // Check if the culture exists in the combo box
                    boolean cultureFound = false;
                    for (String item : cultureComboBox.getItems()) {
                        if (item.startsWith(culture.getId() + ":")) {
                            cultureComboBox.setValue(item);
                            cultureFound = true;
                            break;
                        }
                    }
                    
                    if (!cultureFound) {
                        cultureComboBox.setValue("None");
                    }
                } else {
                    cultureComboBox.setValue("None");
                }
            }
            
            // Set due date
            if (currentTask.getDateEcheance() != null) {
                dueDateField.setText(dateFormat.format(currentTask.getDateEcheance()));
            }
            
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading task", e.getMessage());
        }
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
                
                // If we're loading a task with a culture, we'll set it later
                if (cultureComboBox.getValue() == null) {
                    cultureComboBox.setValue("None");
                }
            }
        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error loading cultures", e.getMessage());
        }
    }

    @FXML
    private void saveTaskChanges(ActionEvent event) {
        if (validateForm()) {
            try {
                Tache updatedTask = createTaskFromForm();
                Tache savedTask = tacheService.update(updatedTask);
                
                if (savedTask != null) {
                    Alerts.showAlert(AlertType.INFORMATION, "Success", "Task updated successfully");
                    App.navigateTo("page_tasks");
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error", "Failed to update task");
                }
            } catch (SQLException | ParseException e) {
                Alerts.showAlert(AlertType.ERROR, "Error updating task", e.getMessage());
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
        // We're updating the existing task
        Tache task = currentTask;
        
        // Set description
        task.setDescription(descriptionField.getText().trim());
        
        // Set status
        task.setStatut(statusComboBox.getValue());
        
        // Set priority
        task.setPriorite(priorityComboBox.getValue());
        
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
        } else {
            // If "None" is selected, set culture to null
            task.setCulture(null);
        }
        
        return task;
    }
}