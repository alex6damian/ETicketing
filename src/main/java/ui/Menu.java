package ui;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import models.*;
import utils.DatabaseConnection;
import utils.PasswordUtils;
import utils.Utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;

public class Menu {
    private static boolean userLoggedIn;
    private static Menu instance;
    private Scanner Scanner;
    protected ArrayList<Event> events;

    public Menu(){
        Scanner = new Scanner(System.in);
        userLoggedIn = false;
    }

    public static Menu getInstance(){
        if(instance == null){
            instance = new Menu();
        }
        return instance;
    }

    public void run()  {
        java.sql.Connection conn = DatabaseConnection.getInstance().getConnection();

        if (conn != null) {
            System.out.println("Database connection is working.");
        } else {
            System.out.println("Database connection failed.");
            return;
        }

        events = new ArrayList<>();
        Concert concert = new Concert("20.10.2025", "18:00", "Bucharest", "Have good time with friends",
                "Beach Please", "Kendrick Lamar", "Rap", 15);
        events.add(concert);

        int option;
        do{
            System.out.println("\n\n=== E-Ticketing Menu ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            option = Scanner.nextInt();
            Scanner.nextLine(); // Consume newline

            switch(option){
                case 1:
                    this.register(conn);
                    break;
                case 2:
                    User user = this.login(conn);
                    if(user.getId() > 0){
                        userLoggedIn = true;
                        this.userMenu(user);
                    }
                    else
                        System.out.println("Login failed. Please try again.");
                    break;
                case 3:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while(option != 3);
    }

    public boolean register(Connection conn) {
        // Regex patterns and matchers
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher emailMatcher;
        Matcher phoneMatcher;


        System.out.println("\n\n=== Register ===");
        System.out.print("Enter name: ");
        String name = Scanner.nextLine();
        System.out.print("Enter email: ");
        String email = Scanner.nextLine();
        emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            System.out.println("Invalid email format.");
            Utils.sleep(1);
            register(conn);
        }
        System.out.print("Enter password: ");
        String password = Scanner.nextLine();
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            Utils.sleep(1);
            register(conn);
        }
        System.out.print("Enter address: ");
        String address = Scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = Scanner.nextLine();
        phoneMatcher = phonePattern.matcher(phoneNumber);
        if (!phoneMatcher.matches()) {
            System.out.println("Invalid phone number format.");
            Utils.sleep(1);
            register(conn);
        }

        Customer customer = new Customer(name, email, password, address, phoneNumber);
        CustomerDAO customerDAO = new CustomerDAO();

        System.out.println("Registering user...");
        if (customerDAO.addUser(customer)) {
            Utils.sleep(1);
            System.out.println("Registration successful!");

        } else {
            Utils.sleep(1);
            System.out.println("Registration failed.");
        }
        Utils.sleep(1);
        return true;
    }

    public User login(Connection conn) {
        System.out.println("\n\n=== Login ===");
        System.out.print("Enter email: ");
        String email = Scanner.nextLine();
        System.out.print("Enter password: ");
        String password = Scanner.nextLine();

        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement smtm = conn.prepareStatement(query)) {
            smtm.setString(1, email);
            ResultSet rs = smtm.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (PasswordUtils.checkPassword(password, hashedPassword)) {
                    System.out.println("Logging in...");
                    Utils.sleep(1);
                    System.out.println("Login successful!");
                    System.out.println("Welcome, " + rs.getString("name") + "!");
                    Utils.sleep(1);

                    String type = rs.getString("user_type");
                    if("Customer".equals(type)){
                        return new Customer(
                                rs.getString("name"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getString("address"),
                                rs.getString("phone")
                        );
                    } else if("Admin".equals(type)) {
                        return new Admin(
                                rs.getString("name"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getString("role")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private void userMenu(User user){
        int option;

        do{
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. My tickets");
            System.out.println("4. Events");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");

            option = Scanner.nextInt();
            Scanner.nextLine(); // Consume newline

            switch(option){
                case 1:
                    System.out.println("Viewing profile...");
                    // Call view profile method
                    break;
                case 2:
                    System.out.println("Updating profile...");
                    // Call update profile method
                    break;
                case 3:
                    // de lucrat
                    break;
                case 4:
                    this.showEvents();
                    break;
                case 5:
                    userLoggedIn = false;
                    System.out.println("Logging out...");
                    Utils.sleep(1);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while(userLoggedIn);
    }

    private void showEvents() {
        System.out.println("\n=== Events ===");
        for (Event event : events) {
            System.out.println(event);
        }
        System.out.println("Select event: ");
        int option;
        option = Scanner.nextInt();
        Scanner.nextLine();
//          La selectarea fiecarui eveniment, se va deschide un tab pentru a vedea
//      detalii si pentru a cumpara bilet
    }
}
