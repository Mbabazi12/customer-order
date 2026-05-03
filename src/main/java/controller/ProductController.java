package controller;

import service.AppStore;
import app.MainApp;
import model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductController {

    @FXML private TableView<Product>        tableProducts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String>  colName, colCategory;
    @FXML private TableColumn<Product, Double>  colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TextField tfSearch, tfQty;
    @FXML private Label     lblCart;

    private ObservableList<Product> allProducts;
    // Shared cart selection — picked up by OrderController
    public static Product selectedProduct;
    public static int     selectedQty;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        allProducts = FXCollections.observableArrayList(AppStore.productsById.values());
        tableProducts.setItems(allProducts);
    }

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
        tableProducts.setItems(allProducts);
    }

    @FXML
    private void onAddToCart() {
        Product p = tableProducts.getSelectionModel().getSelectedItem();
        if (p == null) { AlertHelper.showError("No Selection", "Please select a product first."); return; }
        int qty;
        try {
            qty = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showError("Invalid Quantity", "Enter a positive number for quantity.");
            return;
        }
        selectedProduct = p;
        selectedQty     = qty;
        lblCart.setText("✔ " + p.getProductName() + " x" + qty + " added. Go to Place Order.");
    }

    @FXML
    private void onBack() {
        MainApp.showScene("CustomerDashboard");
    }
}
