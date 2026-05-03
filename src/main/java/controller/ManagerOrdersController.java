package controller;

import service.AppStore;
import app.MainApp;
import exception.InvalidOrderException;
import model.Order;
import model.OrderStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ManagerOrdersController {

    @FXML private TableView<Order>              tableOrders;
    @FXML private TableColumn<Order, Integer>   colOrderId, colQty;
    @FXML private TableColumn<Order, String>    colCustomer, colProduct, colPayment, colStatus;
    @FXML private TableColumn<Order, Double>    colTotal;
    @FXML private ComboBox<OrderStatus>         cbStatus;
    @FXML private Label                         lblMsg;

    private ObservableList<Order> allOrders;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTotal.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().calculateTotal()));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        cbStatus.getItems().addAll(OrderStatus.values());
        cbStatus.setValue(OrderStatus.DELIVERED);

        List<Order> flat = new ArrayList<>();
        for (List<Order> list : AppStore.customerOrders.values()) flat.addAll(list);
        allOrders = FXCollections.observableArrayList(flat);
        tableOrders.setItems(allOrders);
    }

    @FXML
    private void onUpdateStatus() {
        Order selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select an order to update.");
            return;
        }
        try {
            OrderStatus newStatus = cbStatus.getValue();
            if (newStatus == null) throw new InvalidOrderException("Please choose a status.");
            selected.setStatus(newStatus);
            tableOrders.refresh();
            lblMsg.setText("✔ Order #" + selected.getOrderId() + " → " + newStatus);
        } catch (InvalidOrderException e) {
            AlertHelper.showError("Update Failed", e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainApp.showScene("ManagerDashboard");
    }
}
