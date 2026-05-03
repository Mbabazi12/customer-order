package controller;

import service.AppStore;
import app.MainApp;
import exception.InvalidUserException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ManagerAuthController {

    @FXML private PasswordField pfPassword;
    @FXML private Label         lblError;

    @FXML
    private void onLogin() {
        try {
            String pwd = pfPassword.getText();
            if (!AppStore.MANAGER_PASSWORD.equals(pwd))
                throw new InvalidUserException("Wrong password. Access denied.");
            MainApp.showScene("ManagerDashboard");
        } catch (InvalidUserException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainApp.showScene("Welcome");
    }
}
