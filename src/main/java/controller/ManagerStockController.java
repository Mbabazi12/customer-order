package controller;

import service.AppStore;
import app.MainApp;
import model.Product;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManagerStockController {

    @FXML private TableView<Product>              tableStock;
    @FXML private TableColumn<Product, Integer>   colId, colStock;
    @FXML private TableColumn<Product, String>    colName, colCategory;
    @FXML private TableColumn<Product, Double>    colPrice;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableStock.setItems(FXCollections.observableArrayList(AppStore.productsById.values()));
    }

    @FXML
    private void onBack() {
        MainApp.showScene("ManagerDashboard");
    }
}
