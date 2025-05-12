module projet_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mail;
    requires io.github.cdimascio.dotenv.java;

    opens projet_gui.Controllers to javafx.fxml;
    exports projet_gui;
    exports projet_gui.Controllers;
}
