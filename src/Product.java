import java.util.Map;

public class Product {
    private int productId;
    private String productName;
    private double price;

    public Product(int productId, String productName, double price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductId() {
        return productId;
    }

    public static void viewAll(Map<Integer, Product> productsById) {
        System.out.println("\nProducts (Map used for fast lookup):");
        System.out.println("ID\tName\tPrice");
        for (Product p : productsById.values()) {
            System.out.println(p.getProductId() + "\t" + p.getProductName() + "\t" + p.getPrice());
        }
    }
}
