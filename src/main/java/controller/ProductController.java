package controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Product;
import service.AppStore;
import service.CartService;

/**
 * Shows all available products.
 * Customer selects a product, enters a quantity, and adds it to the cart.
 * After adding, they stay on this page to continue shopping.
 * A cart summary label shows how many items are in the cart.
 */
public class ProductController {

    @FXML private TableView<Product>            tableProducts;
    @FXML private TableColumn<Product, Integer> colId, colStock;
    @FXML private TableColumn<Product, String>  colName, colCategory;
    @FXML private TableColumn<Product, Double>  colPrice;
    @FXML private TextField tfSearch, tfQty;
    @FXML private Label     lblCartSummary, lblMsg;

    private ObservableList<Product> allProducts;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        allProducts = FXCollections.observableArrayList(AppStore.productsById.values());
        tableProducts.setItems(allProducts);

        updateCartSummary();
    }

    // -------------------------------------------------------------------------
    // Add selected product to cart
    // -------------------------------------------------------------------------
    @FXML
    private void onAddToCart() {
        lblMsg.setText("");
        Product p = tableProducts.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertHelper.showError("No Selection", "Please select a product first.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Invalid Quantity", "Enter a positive whole number for quantity.");
            return;
        }

        if (p.getStock() < qty) {
            AlertHelper.showError("Insufficient Stock",
                "Only " + p.getStock() + " units available for " + p.getProductName() + ".");
            return;
        }

        CartService.addItem(p, qty);
        tfQty.clear();
        updateCartSummary();
        lblMsg.setText(p.getProductName() + " x" + qty + " added to cart.");
    }

    // -------------------------------------------------------------------------
    // Go to cart page
    // -------------------------------------------------------------------------
    @FXML
    private void onViewCart() {
        if (CartService.getItemCount() == 0) {
            AlertHelper.showWarning("Cart Empty", "Add at least one product before viewing the cart.");
            return;
        }
        MainApp.showScene("Cart");
    }

    // -------------------------------------------------------------------------
    // Search / sort / reset
    // -------------------------------------------------------------------------
    @FXML
    private void onSearch() {
        String q = tfSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) { tableProducts.setItems(allProducts); return; }
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : allProducts)
            if (p.getProductName().toLowerCase().contains(q) || p.getCategory().toLowerCase().contains(q))
                filtered.add(p);
        tableProducts.setItems(filtered);
    }

    @FXML
    private void onSort() {
        List<Product> sorted = new ArrayList<>(tableProducts.getItems());
        sorted.sort(Comparator.comparingDouble(Product::getPrice));
        tableProducts.setItems(FXCollections.observableArrayList(sorted));
    }

    @FXML
    private void onReset() {
        tfSearch.clear();
        lblMsg.setText("");
        tableProducts.setItems(allProducts);
    }

    @FXML
    private void onBack() {
        MainApp.showScene("CustomerDashboard");
    }

    // -------------------------------------------------------------------------
    // Helper: refresh the cart summary label
    // -------------------------------------------------------------------------
    private void updateCartSummary() {
        int count = CartService.getItemCount();
        if (count == 0) {
            lblCartSummary.setText("Cart is empty");
        } else {
            lblCartSummary.setText("Cart: " + count + " item(s)  |  Total: "
                + String.format("%.2f", CartService.getTotal()));
        }
    }
}
