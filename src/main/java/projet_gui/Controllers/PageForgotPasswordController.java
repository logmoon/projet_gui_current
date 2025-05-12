package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.PasswordResetService;
import projet_gui.Utils.Alerts;

import java.sql.SQLException;

public class PageForgotPasswordController extends ControllerBase {

    @FXML
    private TextField emailField;

    @FXML
    private Button sendCodeButton;

    private PasswordResetService passwordResetService;

    @Override
    public void initializePageContent() {
        // Get the service instance
        passwordResetService = PasswordResetService.getInstance();
    }

    @Override
    public boolean canEnter() {
        return AuthService.getInstance().isAuthenticated() == false;
    }

    @FXML
    void backToLogin(ActionEvent event) {
        App.navigateTo("page_login");
    }

    @FXML
    void sendVerificationCode(ActionEvent event) {
        // Validate the email
        if (validateEmail()) {
            try {
                String email = emailField.getText().trim();
                
                // Generate and send verification code
                String code = passwordResetService.generateVerificationCode(email);
                
                // TODO: Send verification code via email
                Alerts.showAlert(Alert.AlertType.INFORMATION, "Verification Code Sent", 
                    "A verification code has been sent to " + email + ".");
                System.out.println("Verification code: " + code);
                
                // Navigate to reset password page with the email
                App.navigateTo("page_reset_password");
                
            } catch (SQLException e) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Error", 
                    "An error occurred: " + e.getMessage());
            }
        }
    }

    private boolean validateEmail() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Validation Error", 
                "Email cannot be empty.");
            return false;
        } 
        
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Validation Error", 
                "Invalid email format.");
            return false;
        }
        
        return true;
    }
}