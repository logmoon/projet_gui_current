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

    private static final String DEFAULT_FALLBACK_FXML = "front";

    private static Object navigationParam; // Ajout pour stocker l'ID ou autre paramètre

    @Override
    public void start(Stage stage) {
        scene = new Scene(new javafx.scene.layout.Pane(), 1280, 720);
        stage.setScene(scene);
        stage.show();

        navigateTo("front");
    }

    public static void navigateTo(String fxml) {
        navigateTo(fxml, DEFAULT_FALLBACK_FXML);
    }

    public static void navigateTo(String fxml, String fallbackFxml) {
        navigateTo(fxml, fallbackFxml, null); // Appel de la nouvelle surcharge avec paramètre null
    }

    public static void navigateTo(String fxml, String fallbackFxml, Object param) {
        try {
            navigationParam = param; // Stocker le paramètre passé (si non null)
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ControllerBase baseController) {
                if (baseController.canEnter()) {
                    scene.setRoot(root);
                    return;
                }
            }

            // Si on ne peut pas entrer ou pas un ControllerBase, utiliser le fallback
            FXMLLoader fallbackLoader = new FXMLLoader(App.class.getResource(fallbackFxml + ".fxml"));
            Parent fallbackRoot = fallbackLoader.load();
            scene.setRoot(fallbackRoot);

        } catch (IOException e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object getNavigationParam() {
        return navigationParam;
    }

    public static void main(String[] args) {
        launch(args);
    }
}