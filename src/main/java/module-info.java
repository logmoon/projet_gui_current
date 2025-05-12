module projet_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.google.zxing;
    requires java.desktop;
    requires javafx.swing;
    requires itextpdf;
    requires com.google.gson;
// Ouvre le package projet_gui.Entities à javafx.base pour permettre l'accès par réflexion
    opens projet_gui.Entities to javafx.base;
    opens projet_gui.Controllers to javafx.fxml;
    exports projet_gui;
    exports projet_gui.Controllers;
}
