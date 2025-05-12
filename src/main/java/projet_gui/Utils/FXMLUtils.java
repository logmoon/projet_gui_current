package projet_gui.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import projet_gui.Controllers.SidebarController;

import java.io.IOException;

public class FXMLUtils {
    
    public static class SidebarResult {
        private final Node view;
        private final SidebarController controller;
        
        public SidebarResult(Node view, SidebarController controller) {
            this.view = view;
            this.controller = controller;
        }
        
        public Node getView() {
            return view;
        }
        
        public SidebarController getController() {
            return controller;
        }
    }
    
    public static SidebarResult loadSidebar(boolean adminSidebar) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(adminSidebar ? "/projet_gui/admin_sidebar.fxml" : "/projet_gui/sidebar.fxml"));
        Node sidebar = loader.load();
        SidebarController controller = loader.getController();
        return new SidebarResult(sidebar, controller);
    }
}