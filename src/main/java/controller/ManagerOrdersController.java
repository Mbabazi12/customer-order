package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import app.MainApp;
import exception.InvalidOrderException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import model.Order;
import model.OrderStatus;
import model.Product;
import service.AppStore;
import service.DataStore;

/**
 * Manager view for all orders.
 *
 * Features:
 *  - View all orders in a table
 *  - Update order status (Pending / Processing / Delivered / Cancelled)
 *  - Cancel an order (with confirmation dialog) — restores stock
 *  - Add a new order manually (customer ID, product ID, qty, payment)
 */
public class ManagerOrdersController {

    // --- Orders table ---
    @FXML private TableView<Order>              tableOrders;
    @FXML private TableColumn<Order, Integer>   colOrderId, colQty;
    @FXML private TableColumn<Order, String>    colCustomer, colProduct, colPayment, colStatus;
    @FXML private TableColumn<Order, Double>    colTotal;

    // --- Status update row ---
    @FXML private ComboBox<OrderStatus> cbStatus;
    @FXML private Label                 lblMsg;

    // --- Add order form ---
    @FXML private TextField tfCustomerId, tfProductId, tfQty, tfPayment;
    @FXML private Label     lblAddMsg;

    private ObservableList<Order> allOrders;

    @FXML
    public void initialize() {
        // Table columns
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTotal.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().calculateTotal()));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Status combo
        cbStatus.getItems().addAll(OrderStatus.values());
        cbStatus.setValue(OrderStatus.PROCESSING);

        loadOrders();
    }

    // -------------------------------------------------------------------------
    // Load / reload all orders from AppStore into the table
    // -------------------------------------------------------------------------
    private void loadOrders() {
        List<Order> flat = new ArrayList<>();
        for (List<Order> list : AppStore.customerOrders.values()) flat.addAll(list);
        allOrders = FXCollections.observableArrayList(flat);
        tableOrders.setItems(allOrders);
    }

    // -------------------------------------------------------------------------
    // Update status of selected order
    // -------------------------------------------------------------------------
    @FXML
    private void onUpdateStatus() {
        lblMsg.setText("");
        Order selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select an order to update.");
            return;
        }
        try {
            OrderStatus newStatus = cbStatus.getValue();
            if (newStatus == null) throw new InvalidOrderException("Please choose a status.");
            selected.setStatus(newStatus);
            DataStore.updateOrder(selected);
            tableOrders.refresh();
            lblMsg.setText("Order #" + selected.getOrderId() + " updated to " + newStatus + ".");
        } catch (InvalidOrderException e) {
            AlertHelper.showError("Update Failed", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Cancel selected order — confirmation dialog, then restore stock
    // -------------------------------------------------------------------------
    @FXML
    private void onCancelOrder() {
        lblMsg.setText("");
        Order selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Please select an order to cancel.");
            return;
        }
        if (selected.getStatus() == OrderStatus.CANCELLED) {
            AlertHelper.showWarning("Already Cancelled", "This order is already cancelled.");
            return;
        }

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Order");
        confirm.setHeaderText(null);
        confirm.setContentText("Cancel Order #" + selected.getOrderId()
            + " for " + selected.getCustomerName() + "?\nStock will be restored.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Restore stock
            Product p = AppStore.productsById.get(selected.getProductId());
            if (p != null) {
                p.setStock(p.getStock() + selected.getQuantity());
                DataStore.updateProduct(p);
            }
            selected.setStatus(OrderStatus.CANCELLED);
            DataStore.updateOrder(selected);
            tableOrders.refresh();
            lblMsg.setText("Order #" + selected.getOrderId() + " cancelled. Stock restored.");
        }
    }

    // -------------------------------------------------------------------------
    // Add a new order manually (manager creates order on behalf of customer)
    // -------------------------------------------------------------------------
    @FXML
    private void onAddOrder() {
        lblAddMsg.setText("");
        try {
            int customerId = parsePositiveInt(tfCustomerId.getText(), "Customer ID");
            int productId  = parsePositiveInt(tfProductId.getText(),  "Product ID");
            int qty        = parsePositiveInt(tfQty.getText(),        "Quantity");
            String payment = tfPayment.getText().trim();
            if (payment.isEmpty()) payment = "Cash";

            // Validate customer
            Customer customer = findCustomerById(customerId);
            if (customer == null)
                throw new InvalidOrderException("No customer found with ID " + customerId + ".");

            // Validate product
            Product product = AppStore.productsById.get(productId);
            if (product == null)
                throw new InvalidOrderException("No product found with ID " + productId + ".");
            if (product.getStock() < qty)
                throw new InvalidOrderException("Insufficient stock. Available: " + product.getStock());

            // Create and save order
            Order o = new Order(AppStore.nextOrderId++, customer, product, qty, payment, null);
            AppStore.customerOrders
                .computeIfAbsent(customerId, k -> new ArrayList<>())
                .add(o);
            product.setStock(product.getStock() - qty);
            DataStore.saveOrder(o);
            DataStore.updateProduct(product);

            // Clear form
            tfCustomerId.clear();
            tfProductId.clear();
            tfQty.clear();
            tfPayment.clear();

            loadOrders();
            lblAddMsg.setText("Order #" + o.getOrderId() + " added successfully.");

        } catch (InvalidOrderException e) {
            lblAddMsg.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainApp.showScene("ManagerDashboard");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private int parsePositiveInt(String text, String field) {
        try {
            int v = Integer.parseInt(text.trim());
            if (v <= 0) throw new InvalidOrderException(field + " must be greater than 0.");
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidOrderException(field + " must be a valid number.");
        }
    }

    private Customer findCustomerById(int id) {
        for (Customer c : AppStore.allCustomers)
            if (c.getCustomerId() == id) return c;
        return null;
    }
}
