package projet_gui.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import projet_gui.App;
import projet_gui.Services.AuthService;
import projet_gui.Services.PasswordResetService;
import projet_gui.Utils.Alerts;

import java.sql.SQLException;

public class PageResetPasswordController extends ControllerBase {

    @FXML
    private TextField emailField;

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button resetPasswordButton;

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
    void resetPassword(ActionEvent event) {
        // Validate inputs
        if (validateInputs()) {
            try {
                String email = emailField.getText().trim();
                String code = codeField.getText().trim();
                String newPassword = passwordField.getText();
                
                // Reset the password
                boolean success = passwordResetService.resetPassword(email, code, newPassword);
                
                if (success) {
                    Alerts.showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Your password has been reset successfully. You can now login with your new password.");
                    
                    // Navigate back to login page
                    App.navigateTo("page_login");
                } else {
                    Alerts.showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to reset password. Please check your verification code and try again.");
                }
                
            } catch (SQLException e) {
                Alerts.showAlert(Alert.AlertType.ERROR, "Error", 
                    "An error occurred: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorMessage.append("Email cannot be empty.\n");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMessage.append("Invalid email format.\n");
        }
        
        if (codeField.getText().trim().isEmpty()) {
            errorMessage.append("Verification code cannot be empty.\n");
        }
        
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (password.isEmpty()) {
            errorMessage.append("Password cannot be empty.\n");
        } else if (password.length() < 6) {
            errorMessage.append("Password must be at least 6 characters long.\n");
        }
        
        if (!password.equals(confirmPassword)) {
            errorMessage.append("Passwords do not match.\n");
        }
        
        if (errorMessage.length() > 0) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }
}