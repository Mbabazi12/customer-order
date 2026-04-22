public class Order {
    private int orderId;
    private Customer customer;
    private Product product;
    private int quantity;
    private String paymentMethod;

    public Order(int orderId, Customer customer, Product product, int quantity, String paymentMethod) {
        this.orderId = orderId;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
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
        System.out.println("Total: " + calculateTotal());
        System.out.println("---------------------------");
    }
}
