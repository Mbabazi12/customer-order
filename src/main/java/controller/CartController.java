package controller;

import app.MainApp;
import exception.InvalidOrderException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.CartItem;
import model.Order;
import model.Product;
import service.AppStore;
import service.CartService;
import service.DataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the customer's cart and handles checkout.
 *
 * The customer can:
 *  - See all items in the cart
 *  - Remove an item
 *  - Increase or decrease quantity
 *  - Choose a payment method and place the order
 */
public class CartController {

    @FXML private TableView<CartItem>              tableCart;
    @FXML private TableColumn<CartItem, String>    colName;
    @FXML private TableColumn<CartItem, Double>    colPrice, colLineTotal;
    @FXML private TableColumn<CartItem, Integer>   colQty;
    @FXML private ComboBox<String>                 cbPayment;
    @FXML private Label                            lblTotal, lblMsg;

    private ObservableList<CartItem> cartItems;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colLineTotal.setCellValueFactory(data ->
            new SimpleObjectProperty<>(data.getValue().getLineTotal()));

        cbPayment.getItems().addAll("Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer");
        cbPayment.setValue("Cash");

        refreshTable();
    }

    // -------------------------------------------------------------------------
    // Remove selected item from cart
    // -------------------------------------------------------------------------
    @FXML
    private void onRemoveItem() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("No Selection", "Select an item to remove.");
            return;
        }
        CartService.removeItem(selected);
        refreshTable();
    }

    // -------------------------------------------------------------------------
    // Increase quantity of selected item by 1
    // -------------------------------------------------------------------------
    @FXML
    private void onIncrease() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Product p = selected.getProduct();
        int newQty = selected.getQuantity() + 1;
        if (newQty > p.getStock()) {
            AlertHelper.showWarning("Stock Limit", "Only " + p.getStock() + " units available.");
            return;
        }
        CartService.updateQuantity(selected, newQty);
        refreshTable();
    }

    // -------------------------------------------------------------------------
    // Decrease quantity of selected item by 1 (removes if reaches 0)
    // -------------------------------------------------------------------------
    @FXML
    private void onDecrease() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        CartService.updateQuantity(selected, selected.getQuantity() - 1);
        refreshTable();
    }

    // -------------------------------------------------------------------------
    // Place order — one Order per cart item
    // -------------------------------------------------------------------------
    @FXML
    private void onCheckout() {
        lblMsg.setText("");

        if (CartService.getItemCount() == 0) {
            AlertHelper.showError("Empty Cart", "Your cart is empty.");
            return;
        }

        String payment = cbPayment.getValue();
        if (payment == null || payment.isEmpty()) {
            AlertHelper.showError("Payment Required", "Please select a payment method.");
            return;
        }

        try {
            List<CartItem> items = new ArrayList<>(CartService.getCart());
            List<Order> placedOrders = new ArrayList<>();

            // Validate all items first before saving any
            for (CartItem item : items) {
                Product p = AppStore.productsById.get(item.getProduct().getProductId());
                if (p == null)
                    throw new InvalidOrderException("Product not found: " + item.getProductName());
                if (p.getStock() < item.getQuantity())
                    throw new InvalidOrderException("Insufficient stock for: " + p.getProductName()
                        + " (available: " + p.getStock() + ")");
            }

            // All valid — save each item as a separate order
            for (CartItem item : items) {
                Product p = AppStore.productsById.get(item.getProduct().getProductId());
                Order o = new Order(
                    AppStore.nextOrderId++,
                    AppStore.currentCustomer,
                    p,
                    item.getQuantity(),
                    payment,
                    null   // defaults to PENDING
                );
                AppStore.customerOrders
                    .computeIfAbsent(AppStore.currentCustomer.getCustomerId(), k -> new ArrayList<>())
                    .add(o);
                p.setStock(p.getStock() - item.getQuantity());
                DataStore.saveOrder(o);
                DataStore.updateProduct(p);
                placedOrders.add(o);
            }

            CartService.clear();
            refreshTable();

            double grandTotal = 0;
            for (Order o : placedOrders) grandTotal += o.calculateTotal();

            AlertHelper.showSuccess("Order Placed",
                placedOrders.size() + " order(s) placed successfully!\n"
                + "Grand total: " + String.format("%.2f", grandTotal));

            MainApp.showScene("MyOrders");

        } catch (InvalidOrderException e) {
            lblMsg.setText(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Continue shopping — go back to product menu
    // -------------------------------------------------------------------------
    @FXML
    private void onContinueShopping() {
        MainApp.showScene("ProductMenu");
    }

    @FXML
    private void onBack() {
        MainApp.showScene("ProductMenu");
    }

    // -------------------------------------------------------------------------
    // Refresh the table and total label
    // -------------------------------------------------------------------------
    private void refreshTable() {
        cartItems = FXCollections.observableArrayList(CartService.getCart());
        tableCart.setItems(cartItems);
        lblTotal.setText("Grand Total:  " + String.format("%.2f", CartService.getTotal()));
    }
}
