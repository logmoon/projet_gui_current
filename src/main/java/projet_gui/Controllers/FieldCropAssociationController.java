package projet_gui.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import projet_gui.App;
import projet_gui.Entities.Culture;
import projet_gui.Entities.Parcelle;
import projet_gui.Entities.ParcelleCulture;
import projet_gui.Services.AuthService;
import projet_gui.Services.ParcelleCultureService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class FieldCropAssociationController extends ControllerBaseWithSidebar {

    @FXML
    private TableView<ParcelleCulture> associationsTable;

    @FXML
    private TableColumn<ParcelleCulture, String> fieldColumn;

    @FXML
    private TableColumn<ParcelleCulture, String> cropColumn;

    @FXML
    private TableColumn<ParcelleCulture, String> statusColumn;

    @FXML
    private TableColumn<ParcelleCulture, String> diseaseColumn;

    @FXML
    private TableColumn<ParcelleCulture, String> dateAddedColumn;

    @FXML
    private TableColumn<ParcelleCulture, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    private ObservableList<ParcelleCulture> associationList = FXCollections.observableArrayList();
    private FilteredList<ParcelleCulture> filteredData;


    @Override
    public void initializePageContent() {
        setupTableColumns();
        loadAssociationsData();
        setupSearchAndFilter();
        System.out.println("Associations loaded, size: " + associationList.size());
    }

    @Override
    public boolean canEnter() {
        boolean isAuthenticated = AuthService.getInstance().isAuthenticated();
        System.out.println("FieldCropAssociationController - User authenticated: " + isAuthenticated);
        return isAuthenticated;
    }
    @FXML
    private void showWeather(ActionEvent event) {
        try {
            // Load the weather FXML with correct path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/projet_gui/weather.fxml"));
            Parent root = loader.load();

            // Create a new stage for the weather window
            Stage weatherStage = new Stage();
            weatherStage.setTitle("Weather Information");
            weatherStage.setScene(new Scene(root, 600, 400)); // Set appropriate dimensions
            weatherStage.initModality(Modality.APPLICATION_MODAL);
            weatherStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open weather window: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        fieldColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getParcelle() != null ? cellData.getValue().getParcelle().getNom() : "N/A"));
        cropColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCulture() != null ? cellData.getValue().getCulture().getNom() : "N/A"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        diseaseColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMaladie() != null ? cellData.getValue().getMaladie().getId() + "" : "N/A"));
        dateAddedColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellData.getValue().getDateAjout())));

        actionsColumn.setCellFactory(new Callback<TableColumn<ParcelleCulture, Void>, TableCell<ParcelleCulture, Void>>() {
            @Override
            public TableCell<ParcelleCulture, Void> call(final TableColumn<ParcelleCulture, Void> param) {
                return new TableCell<ParcelleCulture, Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        deleteButton.setOnAction((ActionEvent event) -> {
                            ParcelleCulture association = getTableView().getItems().get(getIndex());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirm Delete");
                            alert.setHeaderText("Delete Association");
                            alert.setContentText("Are you sure you want to delete this association?");
                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        ParcelleCultureService.getInstance().deleteParcelleCulture(association.getId());
                                        associationList.remove(association);
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "Association deleted successfully.");
                                    } catch (SQLException e) {
                                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete association: " + e.getMessage());
                                    }
                                }
                            });
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });

        associationsTable.setItems(associationList);
    }

    private void setupSearchAndFilter() {
        // Initialize filter options
        filterComboBox.getItems().addAll("All", "Field", "Crop", "Status", "Disease");
        filterComboBox.setValue("All");

        // Wrap the ObservableList in a FilteredList
        filteredData = new FilteredList<>(associationList, p -> true);

        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredData();
        });

        // Add listener to filter combo box
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredData();
        });

        // Bind the SortedList to the TableView
        SortedList<ParcelleCulture> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(associationsTable.comparatorProperty());
        associationsTable.setItems(sortedData);
    }

    private void updateFilteredData() {
        String searchText = searchField.getText().toLowerCase();
        String filter = filterComboBox.getValue();

        filteredData.setPredicate(association -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            switch (filter) {
                case "Field":
                    return association.getParcelle() != null &&
                            association.getParcelle().getNom().toLowerCase().contains(searchText);
                case "Crop":
                    return association.getCulture() != null &&
                            association.getCulture().getNom().toLowerCase().contains(searchText);
                case "Status":
                    return association.getStatut() != null &&
                            association.getStatut().toLowerCase().contains(searchText);
                case "Disease":
                    return association.getMaladie() != null &&
                            (association.getMaladie().getId() + "").contains(searchText);
                default:
                    return (association.getParcelle() != null &&
                            association.getParcelle().getNom().toLowerCase().contains(searchText)) ||
                            (association.getCulture() != null &&
                                    association.getCulture().getNom().toLowerCase().contains(searchText)) ||
                            (association.getStatut() != null &&
                                    association.getStatut().toLowerCase().contains(searchText)) ||
                            (association.getMaladie() != null &&
                                    (association.getMaladie().getId() + "").contains(searchText));
            }
        });
    }

    private void loadAssociationsData() {
        try {
            System.out.println("Loading associations from database...");
            associationList.setAll(ParcelleCultureService.getInstance().getAllParcelleCultures());
            System.out.println("Loaded " + associationList.size() + " associations.");
        } catch (SQLException e) {
            System.err.println("Failed to load associations: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load associations: " + e.getMessage());
            associationList.setAll(FXCollections.observableArrayList(
                    new ParcelleCulture(1, new Parcelle("Field 1", 10.5, 10.5, null, null, "", "NORMAL"), new Culture(1, "Wheat", 5.0, 10.0, "", ""), "HEALTHY", null, new Timestamp(System.currentTimeMillis()))
            ));
            System.out.println("Mock data loaded, size: " + associationList.size());
        }
    }
    @FXML
    private void handleResetFilters() {
        searchField.clear();
        filterComboBox.setValue("All");
    }
    @FXML
    private void addNewAssociation(ActionEvent event) {
        App.navigateTo("page_fieldcropassociation_add", "page_login");
    }

    @FXML
    private void exportToPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(associationsTable.getScene().getWindow());

        if (file != null) {
            try {
                exportTableToPdf(file);
                showAlert(Alert.AlertType.INFORMATION, "Success", "PDF exported successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export PDF: " + e.getMessage());
            }
        }
    }

    private void exportTableToPdf(File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Field-Crop Associations Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);

        PdfPTable pdfTable = new PdfPTable(associationsTable.getColumns().size() - 1); // Exclude actions column
        pdfTable.setWidthPercentage(100);

        // Add headers
        String[] headers = {"Field", "Crop", "Status", "Disease", "Date Added"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            pdfTable.addCell(cell);
        }

        // Add data
        for (ParcelleCulture association : associationsTable.getItems()) {
            pdfTable.addCell(association.getParcelle() != null ? association.getParcelle().getNom() : "N/A");
            pdfTable.addCell(association.getCulture() != null ? association.getCulture().getNom() : "N/A");
            pdfTable.addCell(association.getStatut() != null ? association.getStatut() : "N/A");
            pdfTable.addCell(association.getMaladie() != null ? association.getMaladie().getId() + "" : "N/A");
            pdfTable.addCell(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(association.getDateAjout()));
        }

        document.add(pdfTable);
        document.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}