package service;

import model.Person;

public class Manager extends Person {
    public Manager(String name, String email) {
        super(name, email);
    }

    @Override
    public void displayRole() { System.out.println("Role: Manager"); }
}
