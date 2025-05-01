module projet_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens projet_gui.Controllers to javafx.fxml;
    exports projet_gui;
    exports projet_gui.Controllers;
}
