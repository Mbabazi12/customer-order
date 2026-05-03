package controller;

import service.AppStore;
import app.MainApp;
import model.Customer;
import model.Product;
import model.Order;
import exception.InvalidOrderException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class OrderController {

    @FXML private TextField tfProductId, tfQuantity;
    @FXML private ComboBox<String> cbPayment;
    @FXML private Label lblSummary, lblError;

    @FXML
    public void initialize() {
        cbPayment.getItems().addAll("Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer");
        cbPayment.setValue("Cash");

        // Pre-fill from cart if product was selected in ProductMenu
        if (ProductController.selectedProduct != null) {
            tfProductId.setText(String.valueOf(ProductController.selectedProduct.getProductId()));
            tfQuantity.setText(String.valueOf(ProductController.selectedQty));
        }
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

            // Clear cart
            ProductController.selectedProduct = null;
            ProductController.selectedQty     = 0;

            lblSummary.setText("✔ Order #" + o.getOrderId() + " placed! Total: " +
                               String.format("%.2f", o.calculateTotal()));
            AlertHelper.showSuccess("Order Placed",
                "Order #" + o.getOrderId() + " placed successfully!\nTotal: " +
                String.format("%.2f", o.calculateTotal()));

        } catch (InvalidOrderException e) {
            lblError.setText(e.getMessage());
        } catch (NumberFormatException e) {
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
