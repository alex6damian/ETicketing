package models;

public class Customer extends User {
    protected String  address;
    protected String  phoneNumber;

    public Customer(String name, String email, String password, String address, String phoneNumber) {
        super(name, email, password);
        this.address = address;
        this.phoneNumber = phoneNumber;
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

}
