package projet_gui.Controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import projet_gui.Entities.Culture;
import projet_gui.Services.QRGenerator;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class QrCodeController {
    private static final int QR_CODE_SIZE = 300;
    private final StackPane parent = new StackPane();
    private final Culture culture;

    public QrCodeController(Culture culture) {
        this.culture = culture;
    }

    public void buildUI() {
        QRGenerator qrGenerator;
        try {
            qrGenerator = new QRGenerator(culture, QR_CODE_SIZE, QR_CODE_SIZE, BufferedImage.TYPE_INT_ARGB);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        BufferedImage qrImage = qrGenerator.qrImage();

        try {
            String filename = "crop_qrcode_" + culture.getId() + ".png";
            qrGenerator.save(filename, "PNG");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image image = SwingFXUtils.toFXImage(qrImage, null);
        ImageView imageView = new ImageView(image);
        parent.getChildren().add(imageView);
    }

    public void start(Stage stage) {
        setupStage(stage);
    }

    private void setupStage(Stage stage) {
        Scene scene = new Scene(parent, 640, 480);
        stage.setTitle("Crop QR Code - " + culture.getNom());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
