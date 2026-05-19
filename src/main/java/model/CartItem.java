package model;

/**
 * Represents a single line in the shopping cart.
 * Holds a product reference and the chosen quantity.
 */
public class CartItem {

    private Product product;
    private int     quantity;

    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    public Product getProduct()      { return product; }
    public int     getQuantity()     { return quantity; }
    public void    setQuantity(int q){ this.quantity = q; }

    /** Convenience: product name for TableView PropertyValueFactory. */
    public String getProductName()   { return product.getProductName(); }

    /** Convenience: unit price for TableView. */
    public double getPrice()         { return product.getPrice(); }

    /** Line total for TableView. */
    public double getLineTotal()     { return product.getPrice() * quantity; }
}
