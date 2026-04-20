public class Customer extends Person {
    private int customerId;

    public Customer(int customerId, String name, String email) {
        super(name, email);
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Customer");
    }
}
