package controller;

import service.AppStore;
import app.MainApp;
import model.Order;
import model.OrderStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MyOrdersController {

    @FXML private TableView<Order>              tableOrders;
    @FXML private TableColumn<Order, Integer>   colOrderId;
    @FXML private TableColumn<Order, String>    colProduct, colPayment, colStatus;
    @FXML private TableColumn<Order, Integer>   colQty;
    @FXML private TableColumn<Order, Double>    colTotal;

    private List<Order> myOrders;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("calculateTotal"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Use a computed-value column for total
        colTotal.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().calculateTotal()));

        myOrders = AppStore.currentCustomer.getOrders(AppStore.customerOrders);
        showOrders(myOrders);
    }

    @FXML private void onAll()       { showOrders(myOrders); }
    @FXML private void onPending()   { filterByStatus(OrderStatus.PENDING); }
    @FXML private void onDelivered() { filterByStatus(OrderStatus.DELIVERED); }

    private void filterByStatus(OrderStatus status) {
        ObservableList<Order> filtered = FXCollections.observableArrayList();
        for (Order o : myOrders) if (o.getStatus() == status) filtered.add(o);
        tableOrders.setItems(filtered);
    }

    private void showOrders(List<Order> orders) {
        tableOrders.setItems(FXCollections.observableArrayList(orders));
    }

    @FXML
    private void onBack() {
        MainApp.showScene("CustomerDashboard");
    }
}
