package model;

import java.util.*;

public class Customer extends Person {
    private int customerId;
    private String phone;
    private String address;

    public Customer(int customerId, String name, String email, String phone, String address) {
        super(name, email);
        this.customerId = customerId;
        this.phone      = phone;
        this.address    = address;
    }

    public int    getCustomerId() { return customerId; }
    public String getPhone()      { return phone; }
    public String getAddress()    { return address; }

    @Override
    public void displayRole() { System.out.println("Role: Customer"); }

    public List<Order> getOrders(Map<Integer, List<Order>> customerOrders) {
        return customerOrders.getOrDefault(customerId, new ArrayList<>());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Customer)) return false;
        return customerId == ((Customer) obj).customerId;
    }

    @Override
    public int hashCode() { return Objects.hash(customerId); }
}
