public class Main {
    public static void main(String[] args) {
        Customer customer1 = new Customer(1, "Diane", "diane@gmail.com");
        Customer customer2 = new Customer(2, "Mbabazi", "mbabazi@gmail.com");
        Product product1 = new Product(101, "Laptop", 800000);
        Product product2 = new Product(102, "Phone", 100000);
        Product product3 = new Product(103, "Speaker", 50000);

        customer1.displayRole();

        Order order1 = new Order(1, customer1, product1, 1);
        Order order2 = new Order(2, customer1, product2, 2);
        Order order3 = new Order(3, customer2, product3, 1);

        order1.displayOrder();
        order2.displayOrder();

        customer2.displayRole();
        order3.displayOrder();
    }
}