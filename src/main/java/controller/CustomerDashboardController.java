package controller;

import service.AppStore;
import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CustomerDashboardController {

    @FXML private Label lblWelcome;

    @FXML
    public void initialize() {
        if (AppStore.currentCustomer != null)
            lblWelcome.setText("Welcome, " + AppStore.currentCustomer.getName() + "!");
    }

    @FXML private void onViewMenu()     { MainApp.showScene("ProductMenu"); }
    @FXML private void onPlaceOrder()   { MainApp.showScene("OrderPlacement"); }
    @FXML private void onViewOrders()   { MainApp.showScene("MyOrders"); }
    @FXML private void onCancelOrder()  { MainApp.showScene("CancelOrder"); }
    @FXML private void onFilterOrders() { MainApp.showScene("MyOrders"); }

    @FXML
    private void onLogout() {
        AppStore.currentCustomer = null;
        MainApp.showScene("Welcome");
    }
}
