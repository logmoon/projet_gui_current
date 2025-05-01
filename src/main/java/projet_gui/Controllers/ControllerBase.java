package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import projet_gui.Utils.FXMLUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class ControllerBase implements Initializable {
    
    @FXML
    protected BorderPane mainLayout;
    
    protected SidebarController sidebarController;
    
    /**
     * Determines if the user can enter this page.
     * Used for access control and navigation validation.
     * @return true if the user can enter the page, false otherwise
     */
    public abstract boolean canEnter();

    /**
     * Method for child classes to initialize their specific content.
     * This will be called after sidebar injection.
     */
    protected abstract void initializePageContent();
    
    /**
     * Determines if this controller uses a sidebar.
     * Override and return false in controllers that don't need a sidebar.
     * @return true if the controller should have a sidebar, false otherwise
     */
    protected boolean useSidebar() {
        return false;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Only inject sidebar if this controller uses it and has a mainLayout
        if (useSidebar() && mainLayout != null) {
            injectSidebar();
        }
        
        // Call the method for specific content initialization
        initializePageContent();
    }
    
    
    /**
     * Injects the sidebar into the mainLayout's left region.
     * Also sets the active button based on the current page.
     */
    private void injectSidebar() {
        try {
            // Load sidebar
            // NOTE: WE PASS IN FALSE JUST FOR THE TIME BEING
            // TODO: UPDATE THIS TO CHANGE DEPENDING ON THE USER, This change should be implemented when we create the admin sidebar
            FXMLUtils.SidebarResult sidebarResult = FXMLUtils.loadSidebar(false);
            
            // Set sidebar as the left component of the BorderPane
            mainLayout.setLeft(sidebarResult.getView());
            
            // Save controller reference for later use (just in case)
            sidebarController = sidebarResult.getController();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to inject sidebar in " + getClass().getSimpleName());
        }
    }
}