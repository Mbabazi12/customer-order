package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.Connect;
import model.Product;

public class ProductDAO implements GenericDAO<Product, Integer> {

    @Override
    public void save(Product product) {
        String sql = "INSERT INTO products (product_id, product_name, price, stock, category) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getProductId());
            ps.setString(2, product.getProductName());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getCategory());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[ProductDAO] Error saving product: " + e.getMessage());
        }
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET product_name = ?, price = ?, stock = ?, category = ? " +
                     "WHERE product_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getProductName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setString(4, product.getCategory());
            ps.setInt(5, product.getProductId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[ProductDAO] Error updating product: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[ProductDAO] Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public Product getById(Integer productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("[ProductDAO] Error fetching product by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Product> getAll() {
        String sql = "SELECT * FROM products ORDER BY product_id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("[ProductDAO] Error fetching all products: " + e.getMessage());
        }
        return products;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("product_name"),
            rs.getDouble("price"),
            rs.getInt("stock"),
            rs.getString("category")
        );
    }
}
