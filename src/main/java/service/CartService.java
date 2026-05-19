package service;

import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Product;

/**
 * Manages the current customer's shopping cart in memory.
 *
 * The cart is a simple ArrayList of CartItem objects.
 * It is cleared when the customer logs out or completes checkout.
 */
public class CartService {

    // The active cart — one per session
    private static final List<CartItem> cart = new ArrayList<>();

    private CartService() {}

    // -------------------------------------------------------------------------
    // Add a product to the cart.
    // If the product is already in the cart, increase its quantity.
    // -------------------------------------------------------------------------
    public static void addItem(Product product, int quantity) {
        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cart.add(new CartItem(product, quantity));
    }

    // -------------------------------------------------------------------------
    // Remove a specific item from the cart entirely.
    // -------------------------------------------------------------------------
    public static void removeItem(CartItem item) {
        cart.remove(item);
    }

    // -------------------------------------------------------------------------
    // Update the quantity of an existing cart item.
    // If quantity drops to 0 or below, the item is removed.
    // -------------------------------------------------------------------------
    public static void updateQuantity(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cart.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    // -------------------------------------------------------------------------
    // Return the full cart list (used by CartController to populate the table).
    // -------------------------------------------------------------------------
    public static List<CartItem> getCart() {
        return cart;
    }

    // -------------------------------------------------------------------------
    // Calculate the grand total of all items in the cart.
    // -------------------------------------------------------------------------
    public static double getTotal() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getLineTotal();
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // How many distinct product lines are in the cart.
    // -------------------------------------------------------------------------
    public static int getItemCount() {
        return cart.size();
    }

    // -------------------------------------------------------------------------
    // Clear the cart — called after checkout or logout.
    // -------------------------------------------------------------------------
    public static void clear() {
        cart.clear();
    }
}
