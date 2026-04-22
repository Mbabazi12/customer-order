import java.util.Scanner;
import java.util.List;

public class CustomerHandler {
    private Customer currentCustomer;
    private Scanner scanner = new Scanner(System.in);

    public CustomerHandler(Customer customer) {
        this.currentCustomer = customer;
    }

    public void showMenu() {
        String[] options = {"Products", "Order", "My Orders", "Cancel (Pending)", "Pending", "Delivered", "Logout"};
        while (true) {
            for (int i = 0; i < options.length; i++) {
                System.out.println(i + ". " + options[i]);
            }
            System.out.print("Choose: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 0: Product.viewAll(Main.productsById); break;
                    case 1: placeOrder(); break;
                    case 2: viewMyOrders(); break;
                    case 3: cancelOrder(); break;
                    case 4: viewOrdersByStatus(OrderStatus.PENDING); break;
                    case 5: viewOrdersByStatus(OrderStatus.DELIVERED); break;
                    case 6: return;
                    default: System.out.println("Invalid.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void placeOrder() {
        Product.viewAll(Main.productsById);
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Product p = Main.productsById.get(id);
        if (p == null) {
            System.out.println("Invalid product.");
            return;
        }
        System.out.print("Qty: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        if (qty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }
        System.out.print("Payment: ");
        String pay = scanner.nextLine().trim();
        Order o = new Order(Main.nextOrderId++, currentCustomer, p, qty, pay, null);
        Main.customerOrders.get(currentCustomer.getCustomerId()).add(o);
        o.displayOrder();
    }

    private void viewMyOrders() {
        List<Order> os = currentCustomer.getOrders(Main.customerOrders);
        if (os.isEmpty()) {
            System.out.println("No orders.");
            return;
        }
        System.out.println("\nMy Orders:");
        for (Order o : os) o.displayOrder();
    }

    private void viewOrdersByStatus(OrderStatus st) {
        List<Order> os = currentCustomer.getOrders(Main.customerOrders);
        System.out.println("\n" + st + " Orders:");
        boolean has = false;
        for (Order o : os) {
            if (o.getStatus() == st) {
                o.displayOrder();
                has = true;
            }
        }
        if (!has) System.out.println("None.");
    }

    private void cancelOrder() {
        viewOrdersByStatus(OrderStatus.PENDING);
        System.out.print("Cancel ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        List<Order> os = currentCustomer.getOrders(Main.customerOrders);
        for (Order o : os) {
            if (o.getOrderId() == id && o.getStatus() == OrderStatus.PENDING) {
                os.remove(o);
                System.out.println("Cancelled!");
                return;
            }
        }
        System.out.println("Cannot cancel.");
    }
}
