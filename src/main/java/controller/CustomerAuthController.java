package controller;

import service.AppStore;
import app.MainApp;
import model.Customer;
import exception.InvalidUserException;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class CustomerAuthController {

    @FXML private TextField tfName, tfEmail, tfPhone, tfAddress;
    @FXML private Label     lblStatus;

    @FXML
    private void onLogin() {
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            lblStatus.setText("Name is required to login.");
            return;
        }
        for (Customer c : AppStore.allCustomers) {
            if (c.getName().equalsIgnoreCase(name)) {
                AppStore.currentCustomer = c;
                MainApp.showScene("CustomerDashboard");
                return;
            }
        }
        lblStatus.setText("No account found for \"" + name + "\". Please register.");
    }

    @FXML
    private void onRegister() {
        try {
            String name    = tfName.getText().trim();
            String email   = tfEmail.getText().trim();
            String phone   = tfPhone.getText().trim();
            String address = tfAddress.getText().trim();

            if (name.isEmpty())    throw new InvalidUserException("Name cannot be empty.");
            if (email.isEmpty())   throw new InvalidUserException("Email cannot be empty.");
            if (!email.matches("^[\\w.+\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}$"))
                throw new InvalidUserException("Invalid email format. Example: name@example.com");
            if (phone.isEmpty())   throw new InvalidUserException("Phone cannot be empty.");
            if (address.isEmpty()) throw new InvalidUserException("Address cannot be empty.");

            for (Customer c : AppStore.allCustomers) {
                if (c.getName().equalsIgnoreCase(name))
                    throw new InvalidUserException("An account with this name already exists. Use Login.");
                if (c.getEmail().equalsIgnoreCase(email))
                    throw new InvalidUserException("An account with this email already exists.");
            }

            Customer c = new Customer(AppStore.nextCustomerId++, name, email, phone, address);
            AppStore.allCustomers.add(c);
            AppStore.customerOrders.put(c.getCustomerId(), new ArrayList<>());
            AppStore.currentCustomer = c;
            MainApp.showScene("CustomerDashboard");

        } catch (InvalidUserException e) {
            lblStatus.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainApp.showScene("Welcome");
    }
}
