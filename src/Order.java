import java.util.*;

public class Order {
    private int orderId;
    private Customer customer;
    private Product product;
    private int quantity;
    private String paymentMethod;
    private OrderStatus status;

    public Order(int orderId, Customer customer, Product product, int quantity, String paymentMethod, OrderStatus status) {
        this.orderId = orderId;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.status = (status != null) ? status : OrderStatus.PENDING;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double calculateTotal() {
        return product.getPrice() * quantity;
    }

    public void displayOrder() {
        System.out.println("Order ID: " + orderId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Product: " + product.getProductName());
        System.out.println("Quantity: " + quantity);
        System.out.println("Payment: " + paymentMethod);
        System.out.println("Status: " + status);
        System.out.println("Total: " + calculateTotal());
        System.out.println("---------------------------");
    }

    public int getOrderId() {
        return orderId;
    }

    public static void viewAll(Map<Integer, List<Order>> customerOrders) {
        boolean has = false;
        System.out.println("\nAll Orders (from Map of Lists):");
        for (List<Order> list : customerOrders.values()) {
            for (Order o : list) {
                o.displayOrder();
                has = true;
            }
        }
        if (!has) System.out.println("No orders.");
    }
}

