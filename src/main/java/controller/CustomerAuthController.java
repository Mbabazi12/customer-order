package controller;

import java.util.ArrayList;

import app.MainApp;
import dao.CustomerDAO;
import exception.InvalidUserException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Customer;
import service.AppStore;
import service.CartService;

/**
 * Handles customer authentication.
 *
 * NEW FLOW:
 *  1. Customer enters phone number and clicks "Continue".
 *  2. If phone exists in DB → log in immediately.
 *  3. If phone does NOT exist → show registration form.
 *  4. Customer fills in name, email, address and clicks "Register".
 *  5. Account is created and customer is logged in.
 */
public class CustomerAuthController {

    // --- Phone lookup panel ---
    @FXML private TextField tfPhone;
    @FXML private Label     lblPhoneError;

    // --- Registration panel (hidden until needed) ---
    @FXML private javafx.scene.layout.VBox  regPanel;
    @FXML private TextField tfName, tfEmail, tfRegPhone, tfAddress;
    @FXML private Label     lblRegError;

    private final CustomerDAO customerDAO = new CustomerDAO();

    // Phone entered during lookup — carried into registration
    private String pendingPhone = "";

    @FXML
    public void initialize() {
        // Registration panel is hidden until we know the phone doesn't exist
        regPanel.setVisible(false);
        regPanel.setManaged(false);
    }

    // -------------------------------------------------------------------------
    // Step 1: customer clicks "Continue" after entering phone
    // -------------------------------------------------------------------------
    @FXML
    private void onContinue() {
        lblPhoneError.setText("");
        String phone = tfPhone.getText().trim();

        if (phone.isEmpty()) {
            lblPhoneError.setText("Please enter your phone number.");
            return;
        }
        if (!phone.matches("^[0-9+\\-\\s]{7,20}$")) {
            lblPhoneError.setText("Invalid phone number format.");
            return;
        }

        // Check if customer already exists in memory (fast path)
        Customer found = findInMemory(phone);

        // If not in memory, check the database directly
        if (found == null) {
            found = customerDAO.findByPhone(phone);
            if (found != null) {
                // Sync into AppStore if somehow missing
                AppStore.allCustomers.add(found);
                AppStore.customerOrders.putIfAbsent(found.getCustomerId(), new ArrayList<>());
            }
        }

        if (found != null) {
            // Phone exists → log in
            CartService.clear();
            AppStore.currentCustomer = found;
            MainApp.showScene("ProductMenu");
        } else {
            // Phone not found → show registration form
            pendingPhone = phone;
            tfRegPhone.setText(phone);
            regPanel.setVisible(true);
            regPanel.setManaged(true);
        }
    }

    // -------------------------------------------------------------------------
    // Step 2: customer fills in details and clicks "Register"
    // -------------------------------------------------------------------------
    @FXML
    private void onRegister() {
        lblRegError.setText("");
        try {
            String name    = tfName.getText().trim();
            String email   = tfEmail.getText().trim();
            String phone   = tfRegPhone.getText().trim();
            String address = tfAddress.getText().trim();

            if (name.isEmpty())    throw new InvalidUserException("Name cannot be empty.");
            if (email.isEmpty())   throw new InvalidUserException("Email cannot be empty.");
            if (!email.matches("^[\\w.+\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}$"))
                throw new InvalidUserException("Invalid email format. Example: name@example.com");
            if (phone.isEmpty())   throw new InvalidUserException("Phone cannot be empty.");
            if (address.isEmpty()) throw new InvalidUserException("Address cannot be empty.");

            // Check for duplicate name or email in memory
            for (Customer c : AppStore.allCustomers) {
                if (c.getEmail().equalsIgnoreCase(email))
                    throw new InvalidUserException("An account with this email already exists.");
            }

            // Create and persist the new customer
            Customer c = new Customer(AppStore.nextCustomerId++, name, email, phone, address);
            AppStore.allCustomers.add(c);
            AppStore.customerOrders.put(c.getCustomerId(), new ArrayList<>());
            customerDAO.save(c);

            // Log in
            CartService.clear();
            AppStore.currentCustomer = c;
            MainApp.showScene("ProductMenu");

        } catch (InvalidUserException e) {
            lblRegError.setText(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Cancel registration — go back to phone entry
    // -------------------------------------------------------------------------
    @FXML
    private void onCancelReg() {
        regPanel.setVisible(false);
        regPanel.setManaged(false);
        lblPhoneError.setText("");
        lblRegError.setText("");
        tfPhone.clear();
    }

    @FXML
    private void onBack() {
        MainApp.showScene("Welcome");
    }

    // -------------------------------------------------------------------------
    // Helper: find customer by phone in the in-memory AppStore
    // -------------------------------------------------------------------------
    private Customer findInMemory(String phone) {
        for (Customer c : AppStore.allCustomers) {
            if (c.getPhone().equalsIgnoreCase(phone)) return c;
        }
        return null;
    }
}
