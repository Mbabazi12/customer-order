package controller;

import app.MainApp;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class WelcomeController {

    @FXML
    private void onCustomer() {
        MainApp.showScene("CustomerAuth");
    }

    @FXML
    private void onManager() {
        MainApp.showScene("ManagerLogin");
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
