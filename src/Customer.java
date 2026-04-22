import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class Customer extends Person {
    private int customerId;
    private String phone;
    private String address;

    public Customer(int customerId, String name, String email, String phone, String address) {
        super(name, email);
        this.customerId = customerId;
        this.phone = phone;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Customer");
    }

    public static void viewAll(Set<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customers.");
            return;
        }
        System.out.println("\nAll Customers (Set used for uniqueness):");
        System.out.println("ID Name Email Phone Address");
        for (Customer c : customers) {
            System.out.println(c.getCustomerId() + " " + c.getName() + " " + c.getEmail() + " " + c.getPhone() + " " + c.getAddress());
        }
    }

    public List<Order> getOrders(Map<Integer, List<Order>> customerOrders) {
        return customerOrders.getOrDefault(this.customerId, new ArrayList<>());
    }
}

