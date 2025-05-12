package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FrontController {

    @FXML
    private Button homeButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button productsButton;
    @FXML
    private Button cropsButton;  // New button for crops
    @FXML
    private Button blogButton;
    @FXML
    private Button contactButton;
    @FXML
    private Button quoteButton;
    @FXML
    private Button discoverButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    public void initialize() {
        // Bind image size to stackPane size
        backgroundImageView.fitWidthProperty().bind(stackPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(stackPane.heightProperty());

        // Set up button actions
        discoverButton.setOnAction(event -> {
            System.out.println("Discover button clicked!");
        });

        cropsButton.setOnAction(event -> {
            try {
                // Load the crops frontend view
                Parent cropsView = FXMLLoader.load(getClass().getResource("/projet_gui/CropsFrontend.fxml"));
                Scene cropsScene = new Scene(cropsView);

                // Get the current stage
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene
                currentStage.setScene(cropsScene);
                currentStage.setTitle("Our Crops");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}