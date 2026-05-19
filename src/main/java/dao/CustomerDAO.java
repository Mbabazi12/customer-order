package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.Connect;
import model.Customer;

public class CustomerDAO implements GenericDAO<Customer, Integer> {

    @Override
    public void save(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, name, email, phone, address) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error saving customer: " + e.getMessage());
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? " +
                     "WHERE customer_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getCustomerId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error updating customer: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error deleting customer: " + e.getMessage());
        }
    }

    @Override
    public Customer getById(Integer customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error fetching customer by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Customer> getAll() {
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error fetching all customers: " + e.getMessage());
        }
        return customers;
    }

    // -------------------------------------------------------------------------
    // FIND BY PHONE — used for the new phone-based login
    // -------------------------------------------------------------------------
    public Customer findByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("[CustomerDAO] Error finding customer by phone: " + e.getMessage());
        }
        return null;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
    }
}
