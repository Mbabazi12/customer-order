package service;

import model.*;
import java.util.*;

public class AppStore {

    public static Map<Integer, Product>      productsById   = new HashMap<>();
    public static Set<Customer>              allCustomers   = new HashSet<>();
    public static Map<Integer, List<Order>>  customerOrders = new HashMap<>();
    public static Customer                   currentCustomer = null;
    public static int                        nextCustomerId  = 1;
    public static int                        nextOrderId     = 1;

    public static final String MANAGER_PASSWORD = "admin123";

    private AppStore() {}
}
