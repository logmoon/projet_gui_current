package projet_gui.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Entities.Maladie;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.ParcelleCulture;
import projet_gui.Services.AuthService;
import projet_gui.Services.CultureService;
import projet_gui.Services.ParcelleCultureService;
import projet_gui.Services.ParcelleService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class FieldCropAssociationAddController extends ControllerBaseWithSidebar {

    @FXML
    private ComboBox<Parcelle> fieldComboBox;

    @FXML
    private ComboBox<Culture> cropComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<Maladie> diseaseComboBox;

    @Override
    public void initializePageContent() {
        loadFields();
        loadCrops();
        loadStatuses();
        loadDiseases();
    }

    @Override
    public boolean canEnter() {
        boolean isAuthenticated = AuthService.getInstance().isAuthenticated();
        System.out.println("FieldCropAssociationAddController - User authenticated: " + isAuthenticated);
        return isAuthenticated;
    }

    private void loadFields() {
        try {
            List<Parcelle> fields = ParcelleService.getInstance().getAllParcelles();
            fieldComboBox.setItems(FXCollections.observableArrayList(fields));
            fieldComboBox.setCellFactory(lv -> new ListCell<Parcelle>() {
                @Override
                protected void updateItem(Parcelle item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getNom());
                }
            });
            fieldComboBox.setButtonCell(new ListCell<Parcelle>() {
                @Override
                protected void updateItem(Parcelle item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getNom());
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load fields: " + e.getMessage());
        }
    }

    private void loadCrops() {
        try {
            List<Culture> crops = CultureService.getInstance().getAllCultures();
            cropComboBox.setItems(FXCollections.observableArrayList(crops));
            cropComboBox.setCellFactory(lv -> new ListCell<Culture>() {
                @Override
                protected void updateItem(Culture item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getNom());
                }
            });
            cropComboBox.setButtonCell(new ListCell<Culture>() {
                @Override
                protected void updateItem(Culture item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.getNom());
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load crops: " + e.getMessage());
        }
    }

    private void loadStatuses() {
        statusComboBox.setItems(FXCollections.observableArrayList(
                ParcelleCulture.STATUT_HEALTHY,
                ParcelleCulture.STATUT_SICK,
                ParcelleCulture.STATUT_HARVESTED,
                ParcelleCulture.STATUT_FAILED
        ));
    }

    private void loadDiseases() {
        try {
            List<Maladie> diseases = projet_gui.Services.MaladieService.getInstance().getAllMaladies();
            diseaseComboBox.setItems(FXCollections.observableArrayList(diseases));
            diseaseComboBox.setCellFactory(lv -> new ListCell<Maladie>() {
                @Override
                protected void updateItem(Maladie item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });
            diseaseComboBox.setButtonCell(new ListCell<Maladie>() {
                @Override
                protected void updateItem(Maladie item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNom());
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load diseases: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToAssociations(ActionEvent event) {
        App.navigateTo("page_fieldcropassociation", "page_login");
    }

    @FXML
    private void saveAssociation(ActionEvent event) {
        try {
            Parcelle field = fieldComboBox.getValue();
            Culture crop = cropComboBox.getValue();
            String status = statusComboBox.getValue();
            Maladie disease = diseaseComboBox.getValue();

            if (field == null || crop == null || status == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Field, Crop, and Status are required.");
                return;
            }

            ParcelleCulture association = new ParcelleCulture(field, crop, status, disease, new Timestamp(System.currentTimeMillis()));
            ParcelleCultureService.getInstance().saveParcelleCulture(association);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Association added successfully!");
            App.navigateTo("page_fieldcropassociation", "page_login");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save association: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}