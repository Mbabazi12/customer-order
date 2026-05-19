package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.Connect;
import model.Customer;
import model.Order;
import model.OrderStatus;
import model.Product;
import service.AppStore;

public class OrderDAO implements GenericDAO<Order, Integer> {

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders (order_id, customer_id, product_id, quantity, payment_method, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getOrderId());
            ps.setInt(2, order.getCustomerId());
            ps.setInt(3, order.getProductId());
            ps.setInt(4, order.getQuantity());
            ps.setString(5, order.getPaymentMethod());
            ps.setString(6, order.getStatus().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error saving order: " + e.getMessage());
        }
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET customer_id = ?, product_id = ?, quantity = ?, " +
                     "payment_method = ?, status = ? WHERE order_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getProductId());
            ps.setInt(3, order.getQuantity());
            ps.setString(4, order.getPaymentMethod());
            ps.setString(5, order.getStatus().name());
            ps.setInt(6, order.getOrderId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error deleting order: " + e.getMessage());
        }
    }

    @Override
    public Order getById(Integer orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error fetching order by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getAll() {
        String sql = "SELECT * FROM orders ORDER BY order_id";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = mapRow(rs);
                if (o != null) orders.add(o);
            }

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    public List<Order> getByCustomerId(int customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_id";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order o = mapRow(rs);
                if (o != null) orders.add(o);
            }

        } catch (SQLException e) {
            System.out.println("[OrderDAO] Error fetching orders for customer: " + e.getMessage());
        }
        return orders;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("customer_id");
        int productId  = rs.getInt("product_id");

        Customer customer = findCustomerById(customerId);
        Product  product  = AppStore.productsById.get(productId);

        if (customer == null || product == null) {
            System.out.println("[OrderDAO] Warning: skipping order — customer or product not found.");
            return null;
        }

        return new Order(
            rs.getInt("order_id"),
            customer,
            product,
            rs.getInt("quantity"),
            rs.getString("payment_method"),
            OrderStatus.valueOf(rs.getString("status"))
        );
    }

    private Customer findCustomerById(int id) {
        for (Customer c : AppStore.allCustomers) {
            if (c.getCustomerId() == id) return c;
        }
        return null;
    }
}
