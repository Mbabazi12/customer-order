package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Product;
import service.AppStore;
import service.DataStore;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        DataStore.loadAll();
        seedProductsIfEmpty();
        showScene("Welcome");
        stage.setTitle("Customer Order Management System");
        stage.setResizable(false);
        stage.show();
    }

    /** Navigate to any FXML screen by name (e.g. "Welcome", "CustomerDashboard"). */
    public static void showScene(String fxmlName) {
        try {
            Parent root = FXMLLoader.load(
                MainApp.class.getResource("/fxml/" + fxmlName + ".fxml"));
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DataStore.saveAll();
    }

    private void seedProductsIfEmpty() {
        if (!AppStore.productsById.isEmpty()) return;
        AppStore.productsById.put(101, new Product(101, "Laptop",  800000, 10, "Electronics"));
        AppStore.productsById.put(102, new Product(102, "Phone",   100000, 25, "Electronics"));
        AppStore.productsById.put(103, new Product(103, "Speaker",  50000, 15, "Audio"));
        AppStore.productsById.put(104, new Product(104, "Tablet",  200000, 8,  "Electronics"));
        AppStore.productsById.put(105, new Product(105, "Headset",  30000, 20, "Audio"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
