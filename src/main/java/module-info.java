module projet_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mail;
    requires io.github.cdimascio.dotenv.java;
    requires org.json;

    opens projet_gui.Controllers to javafx.fxml;
    exports projet_gui;
    exports projet_gui.Controllers;
    exports projet_gui.Entities;
}
