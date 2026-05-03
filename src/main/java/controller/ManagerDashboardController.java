package controller;

import app.MainApp;

import javafx.fxml.FXML;

public class ManagerDashboardController {

    @FXML private void onViewCustomers()  { MainApp.showScene("ManagerCustomers"); }
    @FXML private void onViewOrders()     { MainApp.showScene("ManagerOrders"); }
    @FXML private void onViewStock()      { MainApp.showScene("ManagerStock"); }
    @FXML private void onSearchCustomer() { MainApp.showScene("ManagerCustomers"); }
    @FXML private void onUpdateStatus()   { MainApp.showScene("ManagerOrders"); }

    @FXML
    private void onLogout() {
        MainApp.showScene("Welcome");
    }
}
