package model;

public class Product {
    private int    productId;
    private String productName;
    private double price;
    private int    stock;
    private String category;

    public Product(int productId, String productName, double price, int stock, String category) {
        this.productId   = productId;
        this.productName = productName;
        this.price       = price;
        this.stock       = stock;
        this.category    = category;
    }

    // Backward-compatible constructor (no stock/category)
    public Product(int productId, String productName, double price) {
        this(productId, productName, price, 100, "General");
    }

    public int    getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public double getPrice()       { return price; }
    public int    getStock()       { return stock; }
    public String getCategory()    { return category; }

    public void setStock(int stock) { this.stock = stock; }
}
