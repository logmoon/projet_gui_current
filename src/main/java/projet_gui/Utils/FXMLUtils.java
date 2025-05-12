package projet_gui.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class FXMLUtils {
    public static Node loadSidebar(boolean adminSidebar) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(adminSidebar ? "/projet_gui/admin_sidebar.fxml" : "/projet_gui/sidebar.fxml"));
        Node sidebar = loader.load();
        return sidebar;
    }
}