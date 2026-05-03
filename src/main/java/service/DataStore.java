package service;

import model.*;
import java.io.*;
import java.util.*;

public class DataStore {

    private static final String DATA_DIR       = "data";
    private static final String PRODUCTS_FILE  = DATA_DIR + "/products.csv";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.csv";
    private static final String ORDERS_FILE    = DATA_DIR + "/orders.csv";
    private static final String COUNTERS_FILE  = DATA_DIR + "/counters.txt";

    public static void loadAll() {
        new File(DATA_DIR).mkdirs();
        loadProducts();
        loadCustomers();
        loadOrders();
        loadCounters();
    }

    public static void saveAll() {
        new File(DATA_DIR).mkdirs();
        saveProducts();
        saveCustomers();
        saveOrders();
        saveCounters();
    }

    private static void loadProducts() {
        File f = new File(PRODUCTS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;
                int    id       = Integer.parseInt(p[0].trim());
                String name     = p[1].trim();
                double price    = Double.parseDouble(p[2].trim());
                int    stock    = p.length > 3 ? Integer.parseInt(p[3].trim()) : 100;
                String category = p.length > 4 ? p[4].trim() : "General";
                AppStore.productsById.put(id, new Product(id, name, price, stock, category));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("[DataStore] Warning: could not read " + PRODUCTS_FILE);
        }
    }

    private static void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            pw.println("productId,name,price,stock,category");
            for (Product p : AppStore.productsById.values())
                pw.println(p.getProductId() + "," + p.getProductName() + "," +
                           p.getPrice() + "," + p.getStock() + "," + p.getCategory());
        } catch (IOException e) {
            System.out.println("[DataStore] Warning: could not write " + PRODUCTS_FILE);
        }
    }

    private static void loadCustomers() {
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 5) continue;
                int    id    = Integer.parseInt(p[0].trim());
                Customer c = new Customer(id, p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim());
                AppStore.allCustomers.add(c);
                AppStore.customerOrders.put(id, new ArrayList<>());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("[DataStore] Warning: could not read " + CUSTOMERS_FILE);
        }
    }

    private static void saveCustomers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            pw.println("customerId,name,email,phone,address");
            for (Customer c : AppStore.allCustomers)
                pw.println(c.getCustomerId() + "," + c.getName() + "," + c.getEmail() +
                           "," + c.getPhone() + "," + c.getAddress());
        } catch (IOException e) {
            System.out.println("[DataStore] Warning: could not write " + CUSTOMERS_FILE);
        }
    }

    private static void loadOrders() {
        File f = new File(ORDERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 6) continue;
                int         orderId    = Integer.parseInt(p[0].trim());
                int         customerId = Integer.parseInt(p[1].trim());
                int         productId  = Integer.parseInt(p[2].trim());
                int         quantity   = Integer.parseInt(p[3].trim());
                String      payment    = p[4].trim();
                OrderStatus status     = OrderStatus.valueOf(p[5].trim());
                Customer customer = findCustomerById(customerId);
                Product  product  = AppStore.productsById.get(productId);
                if (customer == null || product == null) continue;
                Order o = new Order(orderId, customer, product, quantity, payment, status);
                AppStore.customerOrders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(o);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[DataStore] Warning: could not read " + ORDERS_FILE);
        }
    }

    private static void saveOrders() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            pw.println("orderId,customerId,productId,quantity,paymentMethod,status");
            for (List<Order> orders : AppStore.customerOrders.values())
                for (Order o : orders)
                    pw.println(o.getOrderId() + "," + o.getCustomerId() + "," +
                               o.getProductId() + "," + o.getQuantity() + "," +
                               o.getPaymentMethod() + "," + o.getStatus());
        } catch (IOException e) {
            System.out.println("[DataStore] Warning: could not write " + ORDERS_FILE);
        }
    }

    private static void loadCounters() {
        File f = new File(COUNTERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length < 2) continue;
                switch (parts[0].trim()) {
                    case "nextCustomerId": AppStore.nextCustomerId = Integer.parseInt(parts[1].trim()); break;
                    case "nextOrderId":    AppStore.nextOrderId    = Integer.parseInt(parts[1].trim()); break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("[DataStore] Warning: could not read " + COUNTERS_FILE);
        }
    }

    private static void saveCounters() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(COUNTERS_FILE))) {
            pw.println("nextCustomerId=" + AppStore.nextCustomerId);
            pw.println("nextOrderId="    + AppStore.nextOrderId);
        } catch (IOException e) {
            System.out.println("[DataStore] Warning: could not write " + COUNTERS_FILE);
        }
    }

    private static Customer findCustomerById(int id) {
        for (Customer c : AppStore.allCustomers)
            if (c.getCustomerId() == id) return c;
        return null;
    }
}
