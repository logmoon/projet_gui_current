package projet_gui.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import projet_gui.Services.AuthService;
import projet_gui.Utils.FXMLUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class ControllerBase implements Initializable {
    
    @FXML
    protected BorderPane mainLayout;
    
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
            // Check if the current user is an admin
            boolean isAdmin = false;
            AuthService authService = AuthService.getInstance();
            if (authService.isAuthenticated() && authService.getCurrentToken() != null) {
                isAdmin = authService.getCurrentToken().getUser().isAdmin();
            }
            
            // Load the appropriate sidebar based on user role
            Node view = FXMLUtils.loadSidebar(isAdmin);
            
            // Set sidebar as the left component of the BorderPane
            mainLayout.setLeft(view);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to inject sidebar in " + getClass().getSimpleName());
        }
    }
}