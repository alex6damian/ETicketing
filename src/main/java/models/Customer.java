package models;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    protected String  address;
    protected String  phoneNumber;
    protected List<Ticket<?>> tickets;

    public Customer(String name, String email, String password, double balance, String address, String phoneNumber) {
        super(name, email, password, balance);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.tickets = new ArrayList<Ticket<?>>();
    }

    public Customer(int id, String name, String email, String password, double balance, String address, String phoneNumber) {
        super(id, name, email, password, balance);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.tickets = new ArrayList<Ticket<?>>();
    }


    @Override
    public void displayInfo() {
        System.out.println("Customer ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
        System.out.println("Phone Number: " + phoneNumber);
    }

    @Override
    public String getUserType() {
        return "Customer";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Ticket<?>> getTickets() {
        return tickets;
    }

}
