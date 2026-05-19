package app;

import db.DatabaseInitializer;
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

        // 1. Create database tables if they don't exist yet
        DatabaseInitializer.initialize();

        // 2. Load all data from PostgreSQL into AppStore (in-memory)
        DataStore.loadAll();

        // 3. If no products exist in the DB yet, seed the default ones
        seedProductsIfEmpty();

        // 4. Show the first screen
        showScene("Welcome");
        stage.setTitle("Customer Order Management System");
        stage.setResizable(true);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setWidth(960);
        stage.setHeight(680);
        stage.show();
    }

    /**
     * Navigate to any FXML screen by name (e.g. "Welcome", "CustomerDashboard").
     * Attaches the global dark stylesheet on every navigation.
     */
    public static void showScene(String fxmlName) {
        try {
            Parent root = FXMLLoader.load(
                MainApp.class.getResource("/fxml/" + fxmlName + ".fxml"));

            String css = MainApp.class.getResource("/css/dark.css").toExternalForm();

            if (primaryStage.getScene() == null) {
                Scene scene = new Scene(root);
                scene.getStylesheets().add(css);
                primaryStage.setScene(scene);
            } else {
                primaryStage.getScene().getStylesheets().clear();
                primaryStage.getScene().getStylesheets().add(css);
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Persist any final stock changes to the database on shutdown
        DataStore.saveAll();
    }

    /**
     * Seeds default products into the database if the products table is empty.
     * This only runs once — on first launch.
     */
    private void seedProductsIfEmpty() {
        if (!AppStore.productsById.isEmpty()) return;

        Product[] defaults = {
            new Product(101, "Laptop",  800000, 10, "Electronics"),
            new Product(102, "Phone",   100000, 25, "Electronics"),
            new Product(103, "Speaker",  50000, 15, "Audio"),
            new Product(104, "Tablet",  200000,  8, "Electronics"),
            new Product(105, "Headset",  30000, 20, "Audio")
        };

        for (Product p : defaults) {
            AppStore.productsById.put(p.getProductId(), p);
            DataStore.saveProduct(p);  // persist to PostgreSQL
        }

        System.out.println("[MainApp] Default products seeded into database.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
