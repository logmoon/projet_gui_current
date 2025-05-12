package projet_gui.Controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.ParcelleService;
import projet_gui.Utils.Alerts;
import projet_gui.Utils.DataStore;
import projet_gui.Utils.FileDialogUtils;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.Culture;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PageFieldDetailController extends ControllerBaseWithSidebar {

    @FXML
    private Label fieldTitleLabel;
    
    @FXML
    private StackPane imageContainer;
    
    @FXML
    private ImageView fieldImageView;
    
    @FXML
    private Label noImageLabel;
    
    @FXML
    private TextField fieldNameField;
    
    @FXML
    private TextField longueurField;
    
    @FXML
    private TextField largeurField;
    
    @FXML
    private TextField locationField;
    
    @FXML
    private Label areaLabel;
    
    @FXML
    private Label creationDateLabel;
    
    @FXML
    private TableView<ParcelleCulture> cropsTable;
    
    @FXML
    private TableColumn<ParcelleCulture, String> cropNameColumn;
    
    @FXML
    private TableColumn<ParcelleCulture, String> cropStatusColumn;
    
    @FXML
    private TableColumn<ParcelleCulture, Double> cropWaterNeedsColumn;
    
    @FXML
    private TableColumn<ParcelleCulture, Double> cropNutrientNeedsColumn;
    
    @FXML
    private TableColumn<ParcelleCulture, Void> cropActionsColumn;
    
    @FXML
    private HBox weatherContainer;
    
    private ParcelleService parcelleService;
    private ParcelleCultureService parcelleCultureService;
    private Parcelle currentParcelle;

    @Override
    public void initializePageContent() {
        parcelleService = ParcelleService.getInstance();
        parcelleCultureService = ParcelleCultureService.getInstance();
        
        // Load the selected parcelle
        loadParcelle();
        
        // Setup crops table
        setupCropsTable();
        
        // Load crops data
        loadCropsData();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated();
    }
    
    private void loadParcelle() {
        try {
            Integer selectedParcelleId = DataStore.get("selectedParcelleId", Integer.class);
            if (selectedParcelleId == null) {
                // No parcelle selected, show error and navigate back
                Alerts.showAlert(AlertType.ERROR, "Error", "No field selected");
                App.navigateTo("page_fields");
                return;
            }
            
            currentParcelle = parcelleService.getById(selectedParcelleId);
            if (currentParcelle == null) {
                Alerts.showAlert(AlertType.ERROR, "Error", "Field not found");
                App.navigateTo("page_fields");
                return;
            }
            
            // Update the UI with the parcelle data
            updateUI();
            
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Error", e.getMessage());
            App.navigateTo("page_fields");
        }
    }
    
    private void updateUI() {
        // Update title
        fieldTitleLabel.setText(currentParcelle.getNom());
        
        // Update form fields
        fieldNameField.setText(currentParcelle.getNom());
        longueurField.setText(String.valueOf(currentParcelle.getLongueur()));
        largeurField.setText(String.valueOf(currentParcelle.getLargeur()));
        locationField.setText(currentParcelle.getLocalisationCity());
        
        // Update area
        double area = currentParcelle.getLongueur() * currentParcelle.getLargeur();
        areaLabel.setText(String.format("%.2f m²", area));
        
        // Update creation date
        if (currentParcelle.getDateCreation() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            creationDateLabel.setText(dateFormat.format(currentParcelle.getDateCreation()));
        } else {
            creationDateLabel.setText("-");
        }
        
        // Update image
        updateImage();
    }
    
    private void updateImage() {
        String imagePath = currentParcelle.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                fieldImageView.setImage(image);
                fieldImageView.setVisible(true);
                noImageLabel.setVisible(false);
                return;
            }
        }
        
        // No image or image file doesn't exist
        fieldImageView.setImage(null);
        fieldImageView.setVisible(false);
        noImageLabel.setVisible(true);
    }
    
    private void setupCropsTable() {
        // Setup cell value factories
        cropNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCulture().getNom()));
        
        cropStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut()));
        
        cropWaterNeedsColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getCulture().getBesoinEau()).asObject());
        
        cropNutrientNeedsColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getCulture().getBesoinNutriments()).asObject());
        
        // Actions column with buttons
        cropActionsColumn.setCellFactory(createCropActionsColumnCellFactory());
    }
    
    private Callback<TableColumn<ParcelleCulture, Void>, TableCell<ParcelleCulture, Void>> createCropActionsColumnCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<ParcelleCulture, Void> call(final TableColumn<ParcelleCulture, Void> param) {
                return new TableCell<>() {
                    private final Button removeBtn = new Button("Remove");
                    private final HBox pane = new HBox(5, removeBtn);
                    
                    {
                        removeBtn.getStyleClass().add("button-small");
                        removeBtn.getStyleClass().add("button-danger");
                        pane.setAlignment(Pos.CENTER);
                        
                        removeBtn.setOnAction(event -> {
                            ParcelleCulture parcelleCulture = getTableView().getItems().get(getIndex());
                            removeCrop(parcelleCulture);
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
    
    private void loadCropsData() {
        try {
            if (currentParcelle != null) {
                List<ParcelleCulture> parcelleCultures = parcelleCultureService.getAllByParcelleId(currentParcelle.getId());
                cropsTable.setItems(FXCollections.observableArrayList(parcelleCultures));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Error", "Failed to load crops: " + e.getMessage());
        }
    }
    
    @FXML
    private void navigateToFields(ActionEvent event) {
        App.navigateTo("page_fields");
    }
    
    @FXML
    private void chooseImage(ActionEvent event) {
        String imagePath = FileDialogUtils.showImageChooser((Stage)fieldNameField.getScene().getWindow());
        if (imagePath != null) {
            currentParcelle.setImagePath(imagePath);
            updateImage();
        }
    }
    
    @FXML
    private void saveFieldChanges(ActionEvent event) {
        try {
            // Validate input fields
            if (fieldNameField.getText().trim().isEmpty()) {
                Alerts.showAlert(AlertType.ERROR, "Validation Error", "Field name is required");
                return;
            }
            
            if (locationField.getText().trim().isEmpty()) {
                Alerts.showAlert(AlertType.ERROR, "Validation Error", "Location is required");
                return;
            }
            
            // Validate numeric fields
            double longueur, largeur;
            try {
                longueur = Double.parseDouble(longueurField.getText());
                if (longueur <= 0) {
                    Alerts.showAlert(AlertType.ERROR, "Validation Error", "Length must be a positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                Alerts.showAlert(AlertType.ERROR, "Validation Error", "Please enter a valid number for length");
                return;
            }
            
            try {
                largeur = Double.parseDouble(largeurField.getText());
                if (largeur <= 0) {
                    Alerts.showAlert(AlertType.ERROR, "Validation Error", "Width must be a positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                Alerts.showAlert(AlertType.ERROR, "Validation Error", "Please enter a valid number for width");
                return;
            }
            
            // Update parcelle object
            currentParcelle.setNom(fieldNameField.getText());
            currentParcelle.setLongueur(longueur);
            currentParcelle.setLargeur(largeur);
            currentParcelle.setLocalisationCity(locationField.getText());
            
            // Save to database
            parcelleService.update(currentParcelle);
            
            // Update UI
            updateUI();
            
            // Show success message
            Alerts.showAlert(AlertType.INFORMATION, "Success", "Field updated successfully");
            
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Database Error", "Failed to save field: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @FXML
    private void manageCrops(ActionEvent event) {
        App.navigateTo("page_crops");
    }
    
    private void removeCrop(ParcelleCulture parcelleCulture) {
        try {
            boolean deleted = parcelleCultureService.delete(parcelleCulture.getId());
            if (deleted) {
                // Refresh the table
                loadCropsData();
                Alerts.showAlert(AlertType.INFORMATION, "Success", "Crop removed successfully");
            } else {
                Alerts.showAlert(AlertType.ERROR, "Error", "Failed to remove crop");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert(AlertType.ERROR, "Error", e.getMessage());
        }
    }
}