package controller;

import service.AppStore;
import app.MainApp;
import model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManagerCustomersController {

    @FXML private TableView<Customer>              tableCustomers;
    @FXML private TableColumn<Customer, Integer>   colId;
    @FXML private TableColumn<Customer, String>    colName, colEmail, colPhone, colAddress;
    @FXML private TextField tfSearch;

    private ObservableList<Customer> allCustomers;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        allCustomers = FXCollections.observableArrayList(AppStore.allCustomers);
        tableCustomers.setItems(allCustomers);
    }

    @FXML
    private void onSearch() {
        String q = tfSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) { tableCustomers.setItems(allCustomers); return; }
        ObservableList<Customer> filtered = FXCollections.observableArrayList();
        for (Customer c : allCustomers)
            if (c.getName().toLowerCase().contains(q) || c.getEmail().toLowerCase().contains(q))
                filtered.add(c);
        tableCustomers.setItems(filtered);
    }

    @FXML
    private void onReset() {
        tfSearch.clear();
        tableCustomers.setItems(allCustomers);
    }

    @FXML
    private void onBack() {
        MainApp.showScene("ManagerDashboard");
    }
}
