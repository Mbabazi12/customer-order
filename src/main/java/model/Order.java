package model;

import java.util.*;

public class Order {
    private int         orderId;
    private Customer    customer;
    private Product     product;
    private int         quantity;
    private String      paymentMethod;
    private OrderStatus status;

    public Order(int orderId, Customer customer, Product product,
                 int quantity, String paymentMethod, OrderStatus status) {
        this.orderId       = orderId;
        this.customer      = customer;
        this.product       = product;
        this.quantity      = quantity;
        this.paymentMethod = paymentMethod;
        this.status        = (status != null) ? status : OrderStatus.PENDING;
    }

    public int         getOrderId()       { return orderId; }
    public int         getCustomerId()    { return customer.getCustomerId(); }
    public String      getCustomerName()  { return customer.getName(); }
    public int         getProductId()     { return product.getProductId(); }
    public String      getProductName()   { return product.getProductName(); }
    public int         getQuantity()      { return quantity; }
    public String      getPaymentMethod() { return paymentMethod; }
    public OrderStatus getStatus()        { return status; }
    public Customer    getCustomer()      { return customer; }
    public Product     getProduct()       { return product; }

    public void setStatus(OrderStatus status) {
        if (status != null) this.status = status;
    }

    public double calculateTotal() {
        return product.getPrice() * quantity;
    }

    public static void viewAll(Map<Integer, List<Order>> customerOrders) {
        boolean has = false;
        for (List<Order> list : customerOrders.values())
            for (Order o : list) { System.out.println(o); has = true; }
        if (!has) System.out.println("No orders.");
    }
}
