package controller;

import service.AppStore;
import app.MainApp;
import exception.InvalidOrderException;
import model.Order;
import model.OrderStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CancelOrderController {

    @FXML private TableView<Order>            tablePending;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String>  colProduct, colStatus;
    @FXML private TableColumn<Order, Integer> colQty;
    @FXML private TableColumn<Order, Double>  colTotal;
    @FXML private Label lblMsg;

    private List<Order> myOrders;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(data ->
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().calculateTotal()));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        myOrders = AppStore.currentCustomer.getOrders(AppStore.customerOrders);
        refreshTable();
    }

    @FXML
    private void onCancelSelected() {
        Order selected = tablePending.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select a pending order to cancel.");
            return;
        }
        try {
            if (selected.getStatus() != OrderStatus.PENDING)
                throw new InvalidOrderException("Only PENDING orders can be cancelled.");
            myOrders.remove(selected);
            // Restore stock
            AppStore.productsById.get(selected.getProductId())
                    .setStock(AppStore.productsById.get(selected.getProductId()).getStock() + selected.getQuantity());
            refreshTable();
            lblMsg.setText("✔ Order #" + selected.getOrderId() + " cancelled.");
        } catch (InvalidOrderException e) {
            AlertHelper.showError("Cannot Cancel", e.getMessage());
        }
    }

    private void refreshTable() {
        javafx.collections.ObservableList<Order> pending = FXCollections.observableArrayList();
        for (Order o : myOrders) if (o.getStatus() == OrderStatus.PENDING) pending.add(o);
        tablePending.setItems(pending);
    }

    @FXML
    private void onBack() {
        MainApp.showScene("CustomerDashboard");
    }
}
