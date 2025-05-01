package projet_gui.Utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileDialogUtils {
    public static String showImageChooser(Stage ownerStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Field Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        java.io.File selectedFile = fileChooser.showOpenDialog(ownerStage);
        return selectedFile != null ? selectedFile.getAbsolutePath() : null;
    }
}