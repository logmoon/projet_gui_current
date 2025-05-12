package projet_gui.Services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import projet_gui.Entities.Culture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRGenerator {
    private final BitMatrix bitMatrix;
    private BufferedImage qrImage;

    public QRGenerator(Culture culture, int width, int height, int imageType) throws WriterException {
        String cropData = formatCultureData(culture);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        this.bitMatrix = qrCodeWriter.encode(cropData, BarcodeFormat.QR_CODE, width, height);
        this.qrImageCreate(width, height, imageType);
    }

    private String formatCultureData(Culture culture) {
        return String.format(
                "Crop: %s\nWater Needs: %.2f mm\nNutrient Needs: %.2f kg/ha\nDescription: %s",
                culture.getNom(),
                culture.getBesoinEau(),
                culture.getBesoinNutriments(),
                culture.getDescription()
        );
    }

    public void save(String filePath, String format) throws IOException {
        ImageIO.write(qrImage, format, new File(filePath));
    }

    public BufferedImage qrImage() {
        return qrImage;
    }

    private void qrImageCreate(int width, int height, int imageType) {
        qrImage = new BufferedImage(width, height, imageType);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
    }
}