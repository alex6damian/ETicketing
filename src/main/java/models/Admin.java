package models;

public class Admin extends User {
    private String role;

    public Admin(String name, String email, String password, String role) {
        super(name, email, password);
        this.role = role;
    }

    @Override
    public void displayInfo() {
        System.out.println("Admin ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getUserType() {
        return "Admin";
    }
}
