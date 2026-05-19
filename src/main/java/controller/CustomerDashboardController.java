package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import service.AppStore;
import service.CartService;

public class CustomerDashboardController {

    @FXML private Label lblWelcome;

    @FXML
    public void initialize() {
        if (AppStore.currentCustomer != null)
            lblWelcome.setText("Welcome, " + AppStore.currentCustomer.getName() + "!");
    }

    @FXML private void onViewMenu()     { MainApp.showScene("ProductMenu"); }
    @FXML private void onViewCart()     { MainApp.showScene("Cart"); }
    @FXML private void onViewOrders()   { MainApp.showScene("MyOrders"); }
    @FXML private void onCancelOrder()  { MainApp.showScene("CancelOrder"); }

    @FXML
    private void onLogout() {
        CartService.clear();
        AppStore.currentCustomer = null;
        MainApp.showScene("Welcome");
    }
}
