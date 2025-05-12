package projet_gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import projet_gui.Controllers.ControllerBase;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private static final String DEFAULT_FALLBACK_FXML = "page_login";

    @Override
    public void start(Stage stage) {
        scene = new Scene(new javafx.scene.layout.Pane(), 1280, 720);
        stage.setScene(scene);
        stage.show();

        navigateTo("page_login");
    }

    public static void navigateTo(String fxml) {
        navigateTo(fxml, DEFAULT_FALLBACK_FXML);
    }

    public static void navigateTo(String fxml, String fallbackFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();
            
            Object controller = loader.getController();

            if (controller instanceof ControllerBase baseController) {
                if (baseController.canEnter()) {
                    scene.setRoot(root);
                    return;
                }
            }

            // If we cannot enter or not a ControllerBase, fallback
            FXMLLoader fallbackLoader = new FXMLLoader(App.class.getResource(fallbackFxml + ".fxml"));
            Parent fallbackRoot = fallbackLoader.load();
            scene.setRoot(fallbackRoot);

        } catch (IOException e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
}

    public static void main(String[] args) {
        launch(args);
    }
}