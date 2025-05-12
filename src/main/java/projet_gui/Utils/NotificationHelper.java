package projet_gui.Utils;



import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationHelper {
    private static final Logger logger = Logger.getLogger(NotificationHelper.class.getName());


    public static void showNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            logger.log(Level.WARNING, "System tray not supported");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(
                    NotificationHelper.class.getResource("/images/icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Crop");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override public void run() { tray.remove(trayIcon); }
                    },
                    5000
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Notification error", e);
        }
    }



}