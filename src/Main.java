import java.util.*;

public class Main {
    private static ArrayList<Product> products = new ArrayList<>();
    private static ArrayList<Order> orders = new ArrayList<>();
    private static Customer currentCustomer;
    private static int nextOrderId = 1;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeProducts();
        getCustomerInfo();
        showMenu();
        scanner.close();
    }

    private static void initializeProducts() {
        products.add(new Product(101, "Laptop", 800000));
        products.add(new Product(102, "Phone", 100000));
        products.add(new Product(103, "Speaker", 50000));
    }

    private static void getCustomerInfo() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Enter your name: ");
            name = scanner.nextLine().trim();
        }

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        while (email.isEmpty()) {
            System.out.print("Email cannot be empty. Enter your email: ");
            email = scanner.nextLine().trim();
        }

        currentCustomer = new Customer(1, name, email);
        currentCustomer.displayRole();
        System.out.println("Welcome, " + name + "!\n");
    }

    private static void showMenu() {
        while (true) {
            System.out.println("=== Customer Order System ===");
            System.out.println("1. View Products");
            System.out.println("2. Place Order");
            System.out.println("3. View Past Orders");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        viewProducts();
                        break;
                    case 2:
                        placeOrder();
                        break;
                    case 3:
                        viewOrders();
                        break;
                    case 4:
                        System.out.println("Thank you for using our system!");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1-4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
            } catch (InvalidOrderException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred. Please try again.");
            }
            System.out.println();
        }
    }

    private static void viewProducts() {
        System.out.println("\nAvailable Products:");
        System.out.println("ID\tName\t\tPrice");
        System.out.println("--------------------------------");
        for (Product p : products) {
            System.out.println(p.getProductId() + "\t" + p.getProductName() + "\t\t" + p.getPrice());
        }
    }

    private static void placeOrder() {
        viewProducts();
        System.out.print("Enter product ID: ");
        int prodId = scanner.nextInt();
        scanner.nextLine();

        Product selectedProduct = null;
        for (Product p : products) {
            if (p.getProductId() == prodId) {
                selectedProduct = p;
                break;
            }
        }
        if (selectedProduct == null) {
            throw new InvalidOrderException("Product with ID " + prodId + " does not exist.");
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than 0.");
        }

        System.out.print("Enter payment method (e.g., Cash, Card, Mobile Money): ");
        String paymentMethod = scanner.nextLine().trim();

        Order newOrder = new Order(nextOrderId++, currentCustomer, selectedProduct, quantity, paymentMethod);
        orders.add(newOrder);

        System.out.println("\nProcessing payment...");
        try {
            Thread.sleep(1000); // simulate processing
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Payment successful!");
        newOrder.displayOrder();
    }

    private static void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }
        System.out.println("\nYour Past Orders:");
        for (Order order : orders) {
            order.displayOrder();
        }
    }
}
