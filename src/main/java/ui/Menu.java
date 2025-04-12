package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.*;
import services.TicketService;
import utils.DatabaseConnection;
import utils.PasswordUtils;
import utils.Utils;
import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
        welcomeMessage.getStyleClass().add("custom-label");

        // Create buttons
        Button registerButton = new Button("Register");
        registerButton.setPrefSize(200, 50);
        Button loginButton = new Button("Login");
        loginButton.setPrefSize(200, 50);
        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(200, 50);

        registerButton.getStyleClass().add("button");
        loginButton.getStyleClass().add("button");
        exitButton.getStyleClass().add("button");

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

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

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
                showRegisterMenu(stage);
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
            delay(0, stage);
            buttonMessage.setText("Exiting...");
            });
        stage.show();
    }

    private void showRegisterMenu(Stage stage) {
        VBox form = new VBox(10);

        Label registerMessage = new Label("");
        registerMessage.getStyleClass().add("register-message-label");

        // Create a StackPane and set the background color
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #423f3f;"); // Gray background
        StackPane.setAlignment(registerMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(registerMessage);

        // Name field
        Label nameLabel = new Label("Name:");
        nameLabel.setPrefWidth(150);
        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
        nameField.setPrefWidth(200);

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setPrefWidth(150);
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setPrefWidth(200);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(150);
        javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
        passwordField.setPrefWidth(200);

        // Address field
        Label addressLabel = new Label("Address:");
        addressLabel.setPrefWidth(150);
        javafx.scene.control.TextField addressField = new javafx.scene.control.TextField();
        addressField.setPrefWidth(200);

        // Phone number field
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setPrefWidth(150);
        javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
        phoneField.setPrefWidth(200);

        // Add CSS styles to labels
        nameLabel.getStyleClass().add("register-label");
        emailLabel.getStyleClass().add("register-label");
        passwordLabel.getStyleClass().add("register-label");
        addressLabel.getStyleClass().add("register-label");
        phoneLabel.getStyleClass().add("register-label");

        HBox nameRow = new HBox(10, nameLabel, nameField);
        nameRow.setAlignment(Pos.CENTER);
        HBox emailRow = new HBox(10, emailLabel, emailField);
        emailRow.setAlignment(Pos.CENTER);
        HBox passwordRow = new HBox(10, passwordLabel, passwordField);
        passwordRow.setAlignment(Pos.CENTER);
        HBox addressRow = new HBox(10, addressLabel, addressField);
        addressRow.setAlignment(Pos.CENTER);
        HBox phoneRow = new HBox(10, phoneLabel, phoneField);
        phoneRow.setAlignment(Pos.CENTER);
        form.getChildren().addAll(nameRow, emailRow, passwordRow, addressRow, phoneRow);
        form.setAlignment(Pos.CENTER);

        // Submit button
        Button submitButton = new Button("Register");
        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                registerMessage.setStyle("-fx-text-fill: red;");
                registerMessage.setText("All fields must be filled out!");

                System.out.println("All fields must be filled out!");
                return;
            }

            // Call the register method with the collected data
            Customer customer = new Customer(name, email, password, 5000, address, phone);
            CustomerDAO customerDAO = new CustomerDAO();
            if (customerDAO.addUser(customer)) {

                registerMessage.setStyle("-fx-text-fill: green;");
                registerMessage.setText("Registration successful!");

                delay(1, stage);
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed.");
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(stage));

        // Set button styles
        submitButton.setPrefSize(200, 50);
        backButton.setPrefSize(200, 50);

        // HBox for buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 50, 0));


        // BorderPane for layout
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(form);

        // Add the button box to the bottom of the BorderPane
        borderPane.setBottom(buttonBox);

        stackPane.getChildren().addAll(borderPane);

        // Set the scene
        Scene registerScene = new Scene(stackPane, 1280, 720);
        registerScene.getStylesheets().add("styles.css");
        stage.setScene(registerScene);
    }

    private void showLoginMenu(Stage stage) {
        VBox form = new VBox(10);

        Label loginMessage = new Label("");
        loginMessage.getStyleClass().add("register-message-label");

        // Create a StackPane and set the background color
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #423f3f;"); // Gray background
        StackPane.setAlignment(loginMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(loginMessage);

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setPrefWidth(150);
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setPrefWidth(200);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(150);
        javafx.scene.control.TextField passwordField = new javafx.scene.control.TextField();
        passwordField.setPrefWidth(200);

        emailLabel.getStyleClass().add("register-label");
        passwordLabel.getStyleClass().add("register-label");

        HBox emailRow = new HBox(10, emailLabel, emailField);
        emailRow.setAlignment(Pos.CENTER);
        HBox passwordRow = new HBox(10, passwordLabel, passwordField);
        passwordRow.setAlignment(Pos.CENTER);

        form.getChildren().addAll(emailRow, passwordRow);
        form.setAlignment(Pos.CENTER);

        // Submit button
        Button submitButton = new Button("Login");
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty() ) {
                loginMessage.setStyle("-fx-text-fill: red;");
                loginMessage.setText("All fields must be filled out!");

                System.out.println("All fields must be filled out!");
                return;
            }

            try{
                delay(2, stage);
                User user = login(getConn(), email, password);
                userMenu(user, getConn());
            }
            catch (Exception ex) {
                loginMessage.setStyle("-fx-text-fill: red;");
                loginMessage.setText("Login failed. Please try again.");

                System.out.println("Login failed. Please try again.");
                return;
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(stage));

        // Set button styles
        submitButton.setPrefSize(200, 50);
        backButton.setPrefSize(200, 50);

        // HBox for buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 50, 0));


        // BorderPane for layout
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(form);

        // Add the button box to the bottom of the BorderPane
        borderPane.setBottom(buttonBox);

        stackPane.getChildren().addAll(borderPane);

        // Set the scene
        Scene loginScene = new Scene(stackPane, 1280, 720);
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

                    System.out.println("SUCCESFUL LOGIN");

                    return users.get((rs.getString("email")));
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private void userMenu(User user, Connection conn){

        System.out.println("A INTRAT IN MENIU");

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

    private void delay(int type, Stage stage) {
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            if(type==0) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                        System.out.println("Database connection closed.");
                    }
                } catch (SQLException ex) {
                    System.out.println("Error closing database connection: " + ex.getMessage());
                }
                System.exit(0);
            }
            else if(type==1) {
                start(stage);
            }
            else if(type==2) {
                System.out.println("ceva");
            }
        });
        delay.play();
    }

    public static Connection getConn() {
        return conn;
    }

}
