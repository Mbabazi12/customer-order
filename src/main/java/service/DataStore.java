package service;

import java.util.ArrayList;
import java.util.List;

import dao.CustomerDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import model.Customer;
import model.Order;
import model.Product;

/**
 * DataStore is responsible for loading and saving all application data.
 *
 * Previously used CSV files — now uses PostgreSQL via DAO classes.
 * The rest of the application (controllers, AppStore) is unchanged.
 */
public class DataStore {

    // DAO instances — one per entity
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final ProductDAO  productDAO  = new ProductDAO();
    private static final OrderDAO    orderDAO    = new OrderDAO();

    private DataStore() {}

    // =========================================================================
    // LOAD ALL — called once at startup to populate AppStore from the database
    // =========================================================================
    public static void loadAll() {
        loadProducts();
        loadCustomers();
        loadOrders();
        loadCounters();
    }

    // =========================================================================
    // SAVE ALL — kept for compatibility; individual saves happen in controllers
    // =========================================================================
    public static void saveAll() {
        // Products and customers are saved immediately when created/updated.
        // This method is kept so MainApp.stop() still compiles without changes.
        // Stock changes are persisted in real-time via saveProducts().
        saveProducts();
    }

    // =========================================================================
    // PRODUCTS
    // =========================================================================

    private static void loadProducts() {
        List<Product> products = productDAO.getAll();
        for (Product p : products) {
            AppStore.productsById.put(p.getProductId(), p);
        }
        System.out.println("[DataStore] Loaded " + products.size() + " products from database.");
    }

    /**
     * Persists all current in-memory products to the database.
     * Used at shutdown and after stock changes.
     */
    public static void saveProducts() {
        for (Product p : AppStore.productsById.values()) {
            productDAO.update(p);
        }
    }

    /**
     * Saves a single product to the database (INSERT).
     * Used when seeding initial products.
     */
    public static void saveProduct(Product p) {
        productDAO.save(p);
    }

    /**
     * Updates a single product in the database (UPDATE).
     * Call this after any stock change.
     */
    public static void updateProduct(Product p) {
        productDAO.update(p);
    }

    // =========================================================================
    // CUSTOMERS
    // =========================================================================

    private static void loadCustomers() {
        List<Customer> customers = customerDAO.getAll();
        for (Customer c : customers) {
            AppStore.allCustomers.add(c);
            AppStore.customerOrders.put(c.getCustomerId(), new ArrayList<>());
        }
        System.out.println("[DataStore] Loaded " + customers.size() + " customers from database.");
    }

    /**
     * Saves a newly registered customer to the database (INSERT).
     */
    public static void saveCustomer(Customer c) {
        customerDAO.save(c);
    }

    // =========================================================================
    // ORDERS
    // =========================================================================

    private static void loadOrders() {
        // Products and customers must already be loaded into AppStore before this runs
        List<Order> orders = orderDAO.getAll();
        for (Order o : orders) {
            AppStore.customerOrders
                    .computeIfAbsent(o.getCustomerId(), k -> new ArrayList<>())
                    .add(o);
        }
        System.out.println("[DataStore] Loaded " + orders.size() + " orders from database.");
    }

    /**
     * Saves a newly placed order to the database (INSERT).
     */
    public static void saveOrder(Order o) {
        orderDAO.save(o);
    }

    /**
     * Updates an existing order in the database (e.g. status change).
     */
    public static void updateOrder(Order o) {
        orderDAO.update(o);
    }

    /**
     * Deletes a cancelled order from the database.
     */
    public static void deleteOrder(int orderId) {
        orderDAO.delete(orderId);
    }

    // =========================================================================
    // COUNTERS — derive next IDs from the database instead of a file
    // =========================================================================

    private static void loadCounters() {
        // Derive nextCustomerId from the highest existing customer_id + 1
        List<Customer> customers = new ArrayList<>(AppStore.allCustomers);
        int maxCustomerId = 0;
        for (Customer c : customers) {
            if (c.getCustomerId() > maxCustomerId) maxCustomerId = c.getCustomerId();
        }
        AppStore.nextCustomerId = maxCustomerId + 1;

        // Derive nextOrderId from the highest existing order_id + 1
        List<Order> allOrders = new ArrayList<>();
        for (List<Order> list : AppStore.customerOrders.values()) allOrders.addAll(list);
        int maxOrderId = 0;
        for (Order o : allOrders) {
            if (o.getOrderId() > maxOrderId) maxOrderId = o.getOrderId();
        }
        AppStore.nextOrderId = maxOrderId + 1;

        System.out.println("[DataStore] Next customer ID: " + AppStore.nextCustomerId);
        System.out.println("[DataStore] Next order ID:    " + AppStore.nextOrderId);
    }
}
