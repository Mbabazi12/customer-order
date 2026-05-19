package controller;

import java.util.ArrayList;

import app.MainApp;
import exception.InvalidOrderException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Customer;
import model.Order;
import model.Product;
import service.AppStore;
import service.DataStore;

/**
 * Legacy single-item order placement screen.
 * The primary ordering flow now goes through ProductMenu → Cart → CartController.
 * This controller is kept for backward compatibility and is still reachable
 * from the customer dashboard if needed.
 */
public class OrderController {

    @FXML private TextField    tfProductId, tfQuantity;
    @FXML private ComboBox<String> cbPayment;
    @FXML private Label        lblSummary, lblError;

    @FXML
    public void initialize() {
        cbPayment.getItems().addAll("Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer");
        cbPayment.setValue("Cash");
    }

    @FXML
    private void onPayNow() {
        lblError.setText("");
        lblSummary.setText("");
        try {
            int productId = parsePositiveInt(tfProductId.getText(), "Product ID");
            int qty       = parsePositiveInt(tfQuantity.getText(),  "Quantity");
            String payment = cbPayment.getValue();

            Product p = AppStore.productsById.get(productId);
            if (p == null) throw new InvalidOrderException("No product found with ID " + productId + ".");
            if (p.getStock() < qty)
                throw new InvalidOrderException("Insufficient stock. Available: " + p.getStock());

            Customer customer = AppStore.currentCustomer;
            Order o = new Order(AppStore.nextOrderId++, customer, p, qty, payment, null);
            AppStore.customerOrders
                    .computeIfAbsent(customer.getCustomerId(), k -> new ArrayList<>())
                    .add(o);
            p.setStock(p.getStock() - qty);

            DataStore.saveOrder(o);
            DataStore.updateProduct(p);

            lblSummary.setText("Order #" + o.getOrderId() + " placed! Total: "
                + String.format("%.2f", o.calculateTotal()));
            AlertHelper.showSuccess("Order Placed",
                "Order #" + o.getOrderId() + " placed successfully!\nTotal: "
                + String.format("%.2f", o.calculateTotal()));

        } catch (InvalidOrderException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        MainApp.showScene("CustomerDashboard");
    }

    private int parsePositiveInt(String text, String field) {
        try {
            int v = Integer.parseInt(text.trim());
            if (v <= 0) throw new InvalidOrderException(field + " must be greater than 0.");
            return v;
        } catch (NumberFormatException e) {
            throw new InvalidOrderException(field + " must be a valid number.");
        }
    }
}
