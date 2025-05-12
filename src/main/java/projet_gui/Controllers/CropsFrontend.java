package projet_gui.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import projet_gui.Entities.Culture;
import projet_gui.Services.CultureService;

import java.io.File;
import java.sql.SQLException;

public class CropsFrontend {

    @FXML private FlowPane cropsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button clearFiltersButton;
    @FXML private Pagination paginationControl;

    private ObservableList<Culture> cropList = FXCollections.observableArrayList();
    private FilteredList<Culture> filteredCrops;
    private static final int ITEMS_PER_PAGE = 2;

    @FXML
    public void initialize() {
        setupFilters();
        loadCropsData();
        setupPagination();
    }

    private void setupFilters() {
        filterComboBox.getItems().addAll(
                "All Crops",
                "High Water Needs (>400mm)",
                "Medium Water Needs (200-400mm)",
                "Low Water Needs (<200mm)",
                "High Nutrient Needs (>80kg/ha)",
                "Medium Nutrient Needs (30-80kg/ha)",
                "Low Nutrient Needs (<30kg/ha)"
        );
        filterComboBox.setValue("All Crops");

        filteredCrops = new FilteredList<>(cropList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterCrops());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterCrops());

        clearFiltersButton.setOnAction(e -> {
            searchField.clear();
            filterComboBox.setValue("All Crops");
            filterCrops();
        });
    }

    private void setupPagination() {
        paginationControl.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> displayCrops());
        filteredCrops.addListener((javafx.collections.ListChangeListener.Change<? extends Culture> change) -> updatePagination());
    }

    private void updatePagination() {
        int itemCount = filteredCrops.size();
        int pageCount = (int) Math.ceil((double) itemCount / ITEMS_PER_PAGE);
        paginationControl.setPageCount(Math.max(pageCount, 1));
        paginationControl.setCurrentPageIndex(0);
        displayCrops();
    }

    private void filterCrops() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        filteredCrops.setPredicate(crop -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    crop.getNom().toLowerCase().contains(searchText) ||
                    crop.getDescription().toLowerCase().contains(searchText);

            if (!matchesSearch) return false;

            return switch (filterValue) {
                case "High Water Needs (>400mm)" -> crop.getBesoinEau() > 400;
                case "Medium Water Needs (200-400mm)" -> crop.getBesoinEau() >= 200 && crop.getBesoinEau() <= 400;
                case "Low Water Needs (<200mm)" -> crop.getBesoinEau() < 200;
                case "High Nutrient Needs (>80kg/ha)" -> crop.getBesoinNutriments() > 80;
                case "Medium Nutrient Needs (30-80kg/ha)" -> crop.getBesoinNutriments() >= 30 && crop.getBesoinNutriments() <= 80;
                case "Low Nutrient Needs (<30kg/ha)" -> crop.getBesoinNutriments() < 30;
                default -> true; // "All Crops"
            };
        });

        updatePagination();
    }

    private void loadCropsData() {
        try {
            cropList.setAll(CultureService.getInstance().getAllCultures());
        } catch (SQLException e) {
            System.err.println("Failed to load crops: " + e.getMessage());
            cropList.setAll(FXCollections.observableArrayList(
                    new Culture("Wheat", 500.0, 100.0, "images/wheat.jpg", "A cereal grain"),
                    new Culture("Tomato", 300.0, 50.0, "images/tomato.jpg", "A fruit vegetable"),
                    new Culture("Rice", 600.0, 120.0, "images/rice.jpg", "Staple food crop"),
                    new Culture("Corn", 350.0, 70.0, "images/corn.jpg", "Versatile grain crop"),
                    new Culture("Potato", 250.0, 40.0, "images/potato.jpg", "Root vegetable"),
                    new Culture("Lettuce", 150.0, 20.0, "images/lettuce.jpg", "Leafy green vegetable")
            ));
        }
        filterCrops();
    }

    private void displayCrops() {
        cropsContainer.getChildren().clear();

        int pageIndex = paginationControl.getCurrentPageIndex();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredCrops.size());

        if (fromIndex >= filteredCrops.size() && filteredCrops.size() > 0) {
            fromIndex = 0;
            toIndex = Math.min(ITEMS_PER_PAGE, filteredCrops.size());
            paginationControl.setCurrentPageIndex(0);
        }

        for (int i = fromIndex; i < toIndex; i++) {
            Culture culture = filteredCrops.get(i);
            VBox card = createCropCard(culture);
            cropsContainer.getChildren().add(card);
        }

        if (filteredCrops.isEmpty()) {
            Label noResultsLabel = new Label("No crops match your search/filters.");
            noResultsLabel.getStyleClass().add("no-results-label");
            cropsContainer.getChildren().add(noResultsLabel);
        }
    }

    private VBox createCropCard(Culture culture) {
        VBox card = new VBox(10);
        card.getStyleClass().add("crop-card");
        card.setPrefWidth(250);
        card.setPrefHeight(350);

        ImageView imageView = new ImageView();
        try {
            String imagePath = (culture.getImagePath() != null && new File(culture.getImagePath()).exists())
                    ? new File(culture.getImagePath()).toURI().toString()
                    : getClass().getResource("/images/default_crop.jpg").toExternalForm();

            Image image = new Image(imagePath);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        Label nameLabel = new Label(culture.getNom());
        nameLabel.getStyleClass().add("crop-name");

        Label waterLabel = new Label("Water Needs: " + culture.getBesoinEau() + " mm");
        Label nutrientLabel = new Label("Nutrient Needs: " + culture.getBesoinNutriments() + " kg/ha");

        Text descriptionText = new Text(culture.getDescription());
        descriptionText.setWrappingWidth(230);

        HBox buttonBox = new HBox();
        Button qrButton = new Button("QR Code");
        qrButton.getStyleClass().add("button-qr");
        qrButton.setOnAction(e -> showQRCode(culture));
        buttonBox.getChildren().add(qrButton);

        card.getChildren().addAll(imageView, nameLabel, waterLabel, nutrientLabel, descriptionText, buttonBox);
        card.setPadding(new Insets(10));

        return card;
    }

    private void showQRCode(Culture culture) {
        try {
            QrCodeController qrController = new QrCodeController(culture);
            Stage qrStage = new Stage();
            qrController.buildUI();
            qrController.start(qrStage);
        } catch (Exception e) {
            showAlert("QR Code Error", "Failed to generate QR code: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
