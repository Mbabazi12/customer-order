package controller;

import java.util.List;

import app.MainApp;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Order;
import model.OrderStatus;
import service.AppStore;

/**
 * Shows the current customer's order history.
 * Supports filtering by status (All / Pending / Processing / Delivered / Cancelled).
 */
public class MyOrdersController {

    @FXML private TableView<Order>              tableOrders;
    @FXML private TableColumn<Order, Integer>   colOrderId, colQty;
    @FXML private TableColumn<Order, String>    colProduct, colPayment, colStatus;
    @FXML private TableColumn<Order, Double>    colTotal;

    private List<Order> myOrders;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTotal.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().calculateTotal()));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        myOrders = AppStore.currentCustomer.getOrders(AppStore.customerOrders);
        showOrders(myOrders);
    }

    @FXML private void onAll()        { showOrders(myOrders); }
    @FXML private void onPending()    { filterByStatus(OrderStatus.PENDING); }
    @FXML private void onProcessing() { filterByStatus(OrderStatus.PROCESSING); }
    @FXML private void onDelivered()  { filterByStatus(OrderStatus.DELIVERED); }
    @FXML private void onCancelled()  { filterByStatus(OrderStatus.CANCELLED); }

    private void filterByStatus(OrderStatus status) {
        ObservableList<Order> filtered = FXCollections.observableArrayList();
        for (Order o : myOrders)
            if (o.getStatus() == status) filtered.add(o);
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
