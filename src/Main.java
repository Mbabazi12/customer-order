import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Main {
    // Collections with explanations
    public static Map<Integer, Product> productsById = new HashMap<>(); // Fast lookup by ID
    public static Set<Customer> allCustomers = new HashSet<>(); // Uniqueness
    public static Map<Integer, List<Order>> customerOrders = new HashMap<>(); // One-to-many
    public static Customer currentCustomer;
    public static int nextCustomerId = 1;
    public static int nextOrderId = 1;
    private static Scanner scanner = new Scanner(System.in);
    private static final String MANAGER_PASSWORD = "admin123";

    public static void main(String[] args) {
        initializeProducts();
        roleLoop();
        scanner.close();
    }

    private static void initializeProducts() {
        productsById.put(101, new Product(101, "Laptop", 800000));
        productsById.put(102, new Product(102, "Phone", 100000));
        productsById.put(103, new Product(103, "Speaker", 50000));
    }

    private static void roleLoop() {
        while (true) {
            System.out.println("\n=== Customer Order System ===");
            System.out.println("1. Manager");
            System.out.println("2. Customer");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            int choice = getInt();
            switch (choice) {
                case 1: handleManager(); break;
                case 2: handleCustomer(); break;
                case 0: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    private static int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void handleManager() {
        System.out.print("Password: ");
        if (!MANAGER_PASSWORD.equals(scanner.nextLine().trim())) {
            System.out.println("Access denied.");
            return;
        }
        System.out.println("Manager access!");
        Manager m = new Manager("Admin", "admin@store.com");
        m.displayRole();
        while (true) {
            System.out.println("\n1. Customers 2. Products 3. Orders 0. Exit");
            int choice = getInt();
            switch (choice) {
                case 1: Customer.viewAll(allCustomers); break;
                case 2: Product.viewAll(productsById); break;
                case 3: Order.viewAll(customerOrders); break;
                case 0: return;
            }
        }
    }

    private static void handleCustomer() {
        if (currentCustomer == null) registerCustomer();
        currentCustomer.displayRole();
        while (true) {
            System.out.println("\n1. Products 2. Order 3. Orders 4. Cancel 5. Pending 6. Delivered 0. Logout");
            int choice = getInt();
            switch (choice) {
                case 1: Product.viewAll(productsById); break;
                case 2: placeOrder(); break;
                case 3: viewMyOrders(); break;
                case 4: cancelOrder(); break;
                case 5: viewOrdersByStatus(OrderStatus.PENDING); break;
                case 6: viewOrdersByStatus(OrderStatus.DELIVERED); break;
                case 0: currentCustomer = null; return;
            }
        }
    }

    private static void registerCustomer() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        for (Customer c : allCustomers) if (c.getEmail().equals(email)) {
            System.out.println("Email exists.");
            return;
        }
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();
        currentCustomer = new Customer(nextCustomerId++, name, email, phone, address);
        allCustomers.add(currentCustomer);
        customerOrders.put(currentCustomer.getCustomerId(), new ArrayList<>());
    }

    private static void placeOrder() {
        Product.viewAll(productsById);
        System.out.print("ID: ");
        int id = getInt();
        Product p = productsById.get(id);
        if (p == null) return;
        System.out.print("Qty: ");
        int qty = getInt();
        if (qty <= 0) return;
        System.out.print("Payment: ");
        String pay = scanner.nextLine().trim();
        Order o = new Order(nextOrderId++, currentCustomer, p, qty, pay, null);
        customerOrders.get(currentCustomer.getCustomerId()).add(o);
        o.displayOrder();
    }

    private static void viewMyOrders() {
        List<Order> os = customerOrders.getOrDefault(currentCustomer.getCustomerId(), new ArrayList<>());
        if (os.isEmpty()) {
            System.out.println("No orders.");
            return;
        }
        System.out.println("\nMy Orders:");
        for (Order o : os) o.displayOrder();
    }

    private static void viewOrdersByStatus(OrderStatus st) {
        List<Order> os = customerOrders.getOrDefault(currentCustomer.getCustomerId(), new ArrayList<>());
        System.out.println("\n" + st + " Orders:");
        boolean has = false;
        for (Order o : os) if (o.getStatus() == st) {
            o.displayOrder();
            has = true;
        }
        if (!has) System.out.println("None.");
    }

    private static void cancelOrder() {
        viewOrdersByStatus(OrderStatus.PENDING);
        System.out.print("Cancel ID: ");
        int id = getInt();
        List<Order> os = customerOrders.get(currentCustomer.getCustomerId());
        for (Order o : os) if (o.getOrderId() == id && o.getStatus() == OrderStatus.PENDING) {
            os.remove(o);
            System.out.println("Cancelled.");
            return;
        }
        System.out.println("Cannot.");
    }
}

