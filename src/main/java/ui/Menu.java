package ui;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.*;
import services.TicketService;
import utils.DatabaseConnection;
import utils.PasswordUtils;
import utils.Utils;

//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Menu extends Application {
    private static boolean userLoggedIn;
    private static Menu instance;
    private final Scanner Scanner;
    protected ArrayList<Event> events;
    protected HashMap<String, User> users;
    private static Connection conn;

    public Menu(){
        Scanner = new Scanner(System.in);
        userLoggedIn = false;
        conn = DatabaseConnection.getInstance().getConnection();
        if (conn != null) {
            System.out.println("Database connection is working.");
        } else {
            System.out.println("Database connection failed.");
            return;
        }
    }

    public static Menu getInstance(){
        if(instance == null){
            instance = new Menu();
        }
        return instance;
    }

    @Override
    public void start(Stage stage) {
        // Load the users from the database
        loadUsers(conn);

        // Load the events from the database
        loadEvents(conn);

        showMainMenu(stage);
    }

    public static void main(String[] args) {
        launch(args); // Lansează aplicația JavaFX
    }

    private void showMainMenu(Stage stage) {
        // Create a welcome message
        Label welcomeMessage = new Label("Welcome to our E-Ticketing System!");
        welcomeMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Create buttons
        Button registerButton = new Button("Register");
        registerButton.setPrefSize(200, 50);
        Button loginButton = new Button("Login");
        loginButton.setPrefSize(200, 50);
        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(200, 50);

        // Create a VBox layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(registerButton, loginButton, exitButton);
        vbox.setAlignment(Pos.CENTER);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(welcomeMessage);
        BorderPane.setAlignment(welcomeMessage, Pos.CENTER );
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #35c9c5;");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("E-Ticketing Menu");
        stage.setScene(scene);

        // Set button actions
        registerButton.setOnAction(e -> {
            try {
                register(conn);
            } catch (Exception ex) {
                System.out.println("Error during registration: " + ex.getMessage());
            }
        });

        loginButton.setOnAction(e -> {
            try {
                showLoginMenu(stage);
            } catch (Exception ex) {
                System.out.println("Error during login: " + ex.getMessage());
            }
        });

        exitButton.setOnAction(e -> {
            // Display the exit message
            buttonMessage.setText("Exiting...");

            // Close the database connection and delay the exit to allow the label to be displayed
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            delay.setOnFinished(event -> {
                // Close the database connection
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                        System.out.println("Database connection closed.");
                    }
                } catch (SQLException ex) {
                    System.out.println("Error closing database connection: " + ex.getMessage());
                }
                System.exit(0);
            });
            delay.play();
        });

        stage.show();
    }

    private void showLoginMenu(Stage stage) {
        Label emailLabel = new Label("Email:");
        emailLabel.setPrefWidth(100); // Setează lățimea preferată
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setPrefWidth(200); // Setează lățimea câmpului de text

        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(100); // Setează lățimea preferată
        javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
        passwordField.setPrefWidth(200); // Setează lățimea câmpului de text

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");

        backButton.setOnAction(e -> start(stage)); // Revine la meniul principal

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            User user = login(conn, email, password);
            if (user != null) {
                userLoggedIn = true;
                userMenu(user, conn);
            } else {
                System.out.println("Login failed. Please try again.");
            }
        });

        // Folosește un HBox pentru fiecare rând
        HBox emailRow = new HBox(10, emailLabel, emailField);
        emailRow.setAlignment(Pos.CENTER);

        HBox passwordRow = new HBox(10, passwordLabel, passwordField);
        passwordRow.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(emailRow, passwordRow, loginButton, backButton);
        vbox.setAlignment(Pos.CENTER);

        Scene loginScene = new Scene(vbox, 1280, 720);
        loginScene.getStylesheets().add("styles.css");
        stage.setScene(loginScene);
    }

//    public void run()  {
//
//        if (conn != null) {
//            System.out.println("Database connection is working.");
//        } else {
//            System.out.println("Database connection failed.");
//            return;
//        }
//
//        userLoggedIn = true; // DEBUG
//        this.userMenu(users.get("alex@gmail.com"), conn); // DEBUG
//
//        int option;
//        do{
//            System.out.println("\n\n=== E-Ticketing Menu ===");
//            System.out.println("1. Register");
//            System.out.println("2. Login");
//            System.out.println("3. Exit");
//            System.out.print("Select an option: ");
//
//            option = Scanner.nextInt();
//            Scanner.nextLine(); // Consume newline
//
//            switch(option){
//                case 1:
//                    this.register(conn);
//                    break;
//                case 2:
//                    User user = this.login(conn);
//                    if(user.getId() > 0){
//                        userLoggedIn = true;
//                        this.userMenu(user, conn);
//                    }
//                    else
//                        System.out.println("Login failed. Please try again.");
//                    break;
//                case 3:
//                    System.out.println("Exiting...");
//                    break;
//                default:
//                    System.out.println("Invalid option. Please try again.");
//            }
//        } while(option != 3);
//    }

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
        while (!emailMatcher.matches()) {
            System.out.println("Invalid email format.");
            Utils.sleep(1);
            System.out.print("Enter email: ");
            email = Scanner.nextLine();
            emailMatcher = emailPattern.matcher(email);
        }
        System.out.print("Enter password: ");
        String password = Scanner.nextLine();
        while (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            Utils.sleep(1);
            System.out.print("Enter password: ");
            password = Scanner.nextLine();
        }
        System.out.print("Enter address: ");
        String address = Scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = Scanner.nextLine();
        phoneMatcher = phonePattern.matcher(phoneNumber);
        while (!phoneMatcher.matches()) {
            System.out.println("Invalid phone number format.");
            Utils.sleep(1);
            System.out.print("Enter phone number: ");
            phoneNumber = Scanner.nextLine();
            phoneMatcher = phonePattern.matcher(phoneNumber);
        }

        Customer customer = new Customer(name, email, password, 5000, address, phoneNumber);
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

    public User login(Connection conn, String email, String password) {
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

                    return users.get((rs.getString("email")));
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private void userMenu(User user, Connection conn){
        int option;

        do{
            System.out.println("\n=== User Menu ===");
            System.out.println("Balance: " + user.getBalance() + "$");
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
                    System.out.println("Viewing events...");
                    this.showEvents(conn, user);
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

    private void showEvents(Connection conn, User user) {
        Utils.sleep(1);
        int option;
        System.out.println("\n=== Events ===");
        for (int i=0; i < events.size(); i++) {
            System.out.println(events.get(i));
        }
        System.out.println("\nSelect event to buy ticket: ");
        option = Scanner.nextInt();
        Scanner.nextLine();
        TicketService.getInstance().buyFootballTicket(conn, (Customer) user, (FootballMatch) events.get(option-1), "Gold", 1);
        ((FootballMatch) events.get(option-1)).sellTicket(1);
//          La selectarea fiecarui eveniment, se va deschide un tab pentru a vedea
//      detalii si pentru a cumpara bilet
    }

    private void loadUsers(Connection conn){
        users = new HashMap<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("user_type");
                if(type.equals("Customer")){
                    users.put(rs.getString("email"), new Customer(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("balance"),
                            rs.getString("address"),
                            rs.getString("phone")
                    ));
                } else if(type.equals("Admin")){
                    users.put(rs.getString("email"), new Admin(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("balance"),
                            rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void loadEvents(Connection conn) {
        events = new ArrayList<>();
        String query = "SELECT * FROM Event";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                if ("Concert".equals(type)) {
                    events.add(new Concert(
                            rs.getInt("id"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("location"),
                            rs.getString("description"),
                            rs.getString("eventName"),
                            rs.getString("artist"),
                            rs.getString("genre"),
                            rs.getInt("seatsAvailable")
                    ));
                } else if ("FootballMatch".equals(type)) {
                    events.add(new FootballMatch(
                            rs.getInt("id"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("location"),
                            rs.getString("description"),
                            rs.getString("eventName"),
                            rs.getString("stadiumName"),
                            rs.getInt("seatsAvailable")
                    ));
                } else if ("UFCOnline".equals(type)) {
                    events.add(new UFCOnline(
                            rs.getInt("id"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("location"),
                            rs.getString("description"),
                            rs.getString("eventName"),
                            rs.getString("link"),
                            rs.getString("fightType")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    public static Connection getConn() {
        return conn;
    }
}
