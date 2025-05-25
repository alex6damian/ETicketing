package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;
import services.TicketService;
import utils.DatabaseConnection;
import utils.PasswordUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Application;
import javafx.scene.Scene;

public class Menu extends Application {
    private static boolean userLoggedIn;
    private static User user;
    private static Menu instance;
    private final Scanner Scanner;
    protected ArrayList<Event> events;
    protected HashMap<String, User> users;
    private static Connection conn;

    public Menu(){
        Scanner = new Scanner(System.in);
        userLoggedIn = false;
        conn = DatabaseConnection.getInstance().getConnection();
        events = new ArrayList<>();
        users = new HashMap<>();
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
        if(users.isEmpty()) {
            loadUsers(conn);
            System.out.println(User.getUserCount() + " users loaded from the database.");
        }

        // Load the events from the database
        if(events.isEmpty())
            loadEvents(conn);

//        showMainMenu(stage);
        user = login(getConn(), "test@gmail.com", "parola");
        showUserMenu(user, stage);
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
        buttonMessage.getStyleClass().add("register-message-label");
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
            buttonMessage.setStyle("-fx-text-fill: yellow;");
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
        TextField nameField = new TextField();
        nameField.setPrefWidth(200);

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setPrefWidth(150);
        TextField emailField = new TextField();
        emailField.setPrefWidth(200);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(150);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(200);

        // Address field
        Label addressLabel = new Label("Address:");
        addressLabel.setPrefWidth(150);
        TextField addressField = new TextField();
        addressField.setPrefWidth(200);

        // Phone number field
        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.setPrefWidth(150);
        TextField phoneField = new TextField();
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
                // Add the user to the local users map without loading the whole database again
                users.put(email, customer);

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
        stage.setTitle("Register");
    }

    private void showLoginMenu(Stage stage) {
//      UPDATE
//      De tratat exceptia pentru logarea intr-un cont care nu exista
//      De modificat campul de parola sa nu se vada parola introdusa
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
        TextField emailField = new TextField();
        emailField.setPrefWidth(200);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setPrefWidth(150);
        PasswordField passwordField = new PasswordField();
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
                User userConnected = login(getConn(), email, password);
                if( userConnected == null ) {
                    loginMessage.setStyle("-fx-text-fill: red;");
                    loginMessage.setText("Login failed. Please try again.");

                    System.out.println("Login failed. Please try again.");
                    return;
                }

                setUser(userConnected);
                loginMessage.setStyle("-fx-text-fill: green");
                loginMessage.setText("Logging in...");
                delay(2, stage);
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
        stage.setTitle("Login");
        stage.show();
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
                    System.out.println("Login successful!");
                    System.out.println("Welcome, " + rs.getString("name") + "!");

                    return users.get((rs.getString("email")));
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private void showUserMenu(User user, Stage stage){
        // Create a welcome message
        Label welcomeMessage = new Label("Welcome, " + user.getName() + "!");
        welcomeMessage.getStyleClass().add("custom-label");

        // Create buttons
        Button viewButton = new Button("View profile");
        viewButton.setPrefSize(200, 50);
        Button editButton = new Button("Edit profile");
        editButton.setPrefSize(200, 50);
        Button eventsButton = new Button("Events");
        eventsButton.setPrefSize(200, 50);
        Button ticketsButton = new Button("Tickets");
        ticketsButton.setPrefSize(200, 50);
        Button exitButton = new Button("Logout");
        exitButton.setPrefSize(200, 50);

        viewButton.getStyleClass().add("button");
        editButton.getStyleClass().add("button");
        eventsButton.getStyleClass().add("button");
        ticketsButton.getStyleClass().add("button");
        exitButton.getStyleClass().add("button");


        // Create a VBox layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(viewButton, editButton, eventsButton, ticketsButton, exitButton);
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
        buttonMessage.getStyleClass().add("register-message-label");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        viewButton.setOnAction(e -> {
            //Display profile
            delay(3, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing profile...");
        });

        editButton.setOnAction(e -> {
            // Display the edit profile menu
            delay(12, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Editing profile...");
        });

        eventsButton.setOnAction(e -> {
            // Display the events menu
            delay(8, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing events...");
        });

        ticketsButton.setOnAction(e -> {
            // Display the tickets menu
            delay(4, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing tickets...");
        });

        exitButton.setOnAction(e -> {
            // Display the exit message
            delay(1, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Logging out...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Profile");
        stage.setScene(scene);
        stage.show();
        }

    private void showEditProfile(User user, Stage stage) {
        // Create a header message
        Label editProfileMessage = new Label("Edit My Profile");
        editProfileMessage.getStyleClass().add("custom-label");

        // Create form fields with reduced spacing
        VBox form = new VBox(8);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(500);

        // Status message label
        Label statusMessage = new Label("");
        statusMessage.getStyleClass().add("register-message-label");

        // Create profile fields for editing
        String[] labels = {"Name", "Email", "Address", "Phone"};
        String[] values = {
                user.getName(),
                user.getEmail(),
                ((Customer) user).getAddress(),
                ((Customer) user).getPhoneNumber()
        };

        TextField[] fields = new TextField[4];

        // Create a card for each editable field with reduced size
        for (int i = 0; i < labels.length; i++) {
            VBox card = new VBox(3);
            card.setStyle("-fx-background-color: #35393e; -fx-background-radius: 10; -fx-padding: 10;");

            Label fieldLabel = new Label(labels[i]);
            fieldLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #35c9c5; -fx-font-weight: bold;");

            fields[i] = new TextField(values[i]);
            fields[i].setStyle("-fx-background-color: #252525; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;");

            card.getChildren().addAll(fieldLabel, fields[i]);
            form.getChildren().add(card);
        }

        // Add field for changing password with reduced size
        VBox passwordCard = new VBox(3);
        passwordCard.setStyle("-fx-background-color: #35393e; -fx-background-radius: 10; -fx-padding: 10;");

        Label passwordLabel = new Label("New Password (leave empty to keep current)");
        passwordLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #35c9c5; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #252525; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;");

        passwordCard.getChildren().addAll(passwordLabel, passwordField);
        form.getChildren().add(passwordCard);

        // Create buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save Changes");
        saveButton.setPrefSize(200, 40);
        saveButton.getStyleClass().add("button");

        Button backButton = new Button("Cancel");
        backButton.setPrefSize(200, 40);
        backButton.getStyleClass().add("button");

        buttonBox.getChildren().addAll(saveButton, backButton);

        // Add form and buttons to main layout with reduced spacing
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(form, buttonBox);
        mainLayout.setPadding(new Insets(20, 0, 30, 0));

        // Create layout structure
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(editProfileMessage);
        BorderPane.setAlignment(editProfileMessage, Pos.CENTER);
        borderPane.setCenter(mainLayout);

        // Create a StackPane to overlay the message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);
        stackPane.setStyle("-fx-background-color: #423f3f;");

        StackPane.setAlignment(statusMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(statusMessage);

        // Handle save button action
        saveButton.setOnAction(e -> {
            String name = fields[0].getText().trim();
            String email = fields[1].getText().trim();
            String address = fields[2].getText().trim();
            String phone = fields[3].getText().trim();
            String password = passwordField.getText().trim();

            // Validate inputs
            if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                statusMessage.setStyle("-fx-text-fill: red;");
                statusMessage.setText("All fields except password must be filled out!");
                return;
            }

            // Email validation using regex
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                statusMessage.setStyle("-fx-text-fill: red;");
                statusMessage.setText("Please enter a valid email address!");
                return;
            }

            // Phone validation
            if (!phone.matches("\\d{10}")) {
                statusMessage.setStyle("-fx-text-fill: red;");
                statusMessage.setText("Phone number must be 10 digits!");
                return;
            }

            // Update user in database
            try {
                boolean success = UserDAO.updateUserProfile(conn, user, name, email, address, phone, password);
                if (success) {
                    // Update the local user object
                    user.setName(name);
                    user.setEmail(email);
                    ((Customer) user).setAddress(address);
                    ((Customer) user).setPhoneNumber(phone);

                    statusMessage.setStyle("-fx-text-fill: green;");
                    statusMessage.setText("Profile updated successfully!");
                    delay(2, stage);
                } else {
                    statusMessage.setStyle("-fx-text-fill: red;");
                    statusMessage.setText("Failed to update profile. Please try again.");
                }
            } catch (Exception ex) {
                statusMessage.setStyle("-fx-text-fill: red;");
                statusMessage.setText("Error: " + ex.getMessage());
            }
        });

        // Handle back button action
        backButton.setOnAction(e -> {
            statusMessage.setStyle("-fx-text-fill: yellow;");
            statusMessage.setText("Returning to profile...");
            delay(3, stage);
        });

        // Set the scene directly with stackPane
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Edit Profile");
        stage.setScene(scene);
        stage.show();
    }

    private void showProfile(Stage stage) {
        // Create a welcome message
        Label profileMessage = new Label("My Profile");
        profileMessage.getStyleClass().add("custom-label");

        // Create styled info cards with reduced spacing
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setMaxWidth(600);

        // Create styled profile details with icons
        String[] labels = {"Name", "Email", "Address", "Phone"};
        String[] values = {
                getUser().getName(),
                getUser().getEmail(),
                ((Customer) user).getAddress(),
                ((Customer) user).getPhoneNumber()
        };

        for (int i = 0; i < labels.length; i++) {
            // Create card for each detail with reduced padding
            VBox card = new VBox(3);
            card.setStyle("-fx-background-color: #35393e; -fx-background-radius: 10; -fx-padding: 10;");

            // Field label
            Label fieldLabel = new Label(labels[i]);
            fieldLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #35c9c5; -fx-font-weight: bold;");

            // Field value
            Label fieldValue = new Label(values[i]);
            fieldValue.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-wrap-text: true;");
            fieldValue.setMaxWidth(550);

            card.getChildren().addAll(fieldLabel, fieldValue);
            infoBox.getChildren().add(card);
        }

        // Add balance display
        VBox balanceCard = new VBox(3);
        balanceCard.setStyle("-fx-background-radius: 10; -fx-padding: 10;");
        if(getUser().getBalance() < 1000)
            balanceCard.getStyleClass().add("red-balance-card");
        else
            balanceCard.getStyleClass().add("green-balance-card");

        Label balanceLabel = new Label("Current Balance");
        balanceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333; -fx-font-weight: bold;");

        Label balanceValue = new Label("$" + getUser().getBalance());
        balanceValue.setStyle("-fx-font-size: 20px; -fx-text-fill: #333; -fx-font-weight: bold;");

        balanceCard.getChildren().addAll(balanceLabel, balanceValue);
        infoBox.getChildren().add(balanceCard);

        // Back button with styling
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 40);
        backButton.getStyleClass().add("button");

        // Container for the profile content with reduced spacing
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(infoBox, backButton);
        contentBox.setPadding(new Insets(20, 0, 30, 0));

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(profileMessage);
        BorderPane.setAlignment(profileMessage, Pos.CENTER);
        borderPane.setCenter(contentBox);

        // Create a StackPane for the layout
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);
        stackPane.setStyle("-fx-background-color: #423f3f;");

        // Create a label for messages
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
            delay(2, stage);
        });

        // Set the scene directly with stackPane
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("My Profile");
        stage.setScene(scene);
        stage.show();
    }

    // Tickets submenu
    private void showTicketsMenu(Stage stage) {
        // Create a welcome message
        Label ticketsMessage = new Label("Tickets menu");
        ticketsMessage.getStyleClass().add("custom-label");

        Button viewButton = new Button("View tickets");
        viewButton.setPrefSize(200, 50);

        Button transferButton = new Button("Transfer tickets");
        transferButton.setPrefSize(200, 50);

        Button sellButton = new Button("Sell tickets");
        sellButton.setPrefSize(200, 50);

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Create a VBox layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(ticketsMessage, viewButton, transferButton, sellButton, backButton);
        vbox.setAlignment(Pos.CENTER);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(ticketsMessage);
        BorderPane.setAlignment(ticketsMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        viewButton.setOnAction(e -> {
            delay(5, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing tickets...");
        });

        transferButton.setOnAction(e -> {
            delay(6, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Transferring tickets...");
        });

        sellButton.setOnAction(e -> {
            delay(7, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Selling tickets...");
        });

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(2, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("My tickets");
        stage.setScene(scene);
    }

    private void showTickets(Stage stage) {
        // Create a welcome message
        Label ticketsMessage = new Label("View tickets details");
        ticketsMessage.getStyleClass().add("custom-label");

        List<Ticket> tickets = getUserTickets(conn);
        
        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        for (Ticket ticket : tickets) {
            Button ticketButton = new Button(ticket.getEvent().getEventName());
            ticketButton.setPrefSize(400, 50);
            ticketButton.setOnAction(e -> {
                // Display ticket details
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ticket Details");
                alert.setHeaderText("Details for: " + ticket.getEvent().getEventName());
                alert.setContentText(ticket.toString());
                alert.showAndWait();
            });
            vbox.getChildren().add(ticketButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(ticketsMessage);
        BorderPane.setAlignment(ticketsMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(4, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("View tickets");
        stage.setScene(scene);
    }

    private void transferTicket(Stage stage) {
        // Create a welcome message
        Label ticketsMessage = new Label("Transfer tickets");
        ticketsMessage.getStyleClass().add("custom-label");

        List<Ticket> tickets = getUserTickets(conn);

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");

        for (Ticket ticket : tickets) {
            Button ticketButton = new Button(ticket.getEvent().getEventName());
            ticketButton.setPrefSize(400, 50);
            ticketButton.setOnAction(e -> {
                // Create a dialog to input the recipient's email
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Transfer Ticket");
                dialog.setHeaderText("Transfer Ticket: " + ticket.getEvent().getEventName());
                dialog.setContentText("Enter the recipient's email:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(email -> {
                    // Logic to transfer the ticket
                    if(users.get(email) == null) {
                        buttonMessage.setStyle("-fx-text-fill: red;");
                        buttonMessage.setText("User not found!");
                        System.out.println("User not found!");
                        return;
                    }
                    System.out.println("Transferring ticket to: " + email);
                    TicketService.transferTicket(conn, ticket.getId(), users.get(email));

                    buttonMessage.setStyle("-fx-text-fill: green;");
                    buttonMessage.setText("Ticket transferred to: " + email);

                    // Reload the tickets
                    delay(6, stage);

                });
            });
            vbox.getChildren().add(ticketButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(ticketsMessage);
        BorderPane.setAlignment(ticketsMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(4, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Transfer tickets");
        stage.setScene(scene);
    }

    private void sellTickets(Stage stage) {
        // Create a welcome message
        Label ticketsMessage = new Label("Sell tickets");
        ticketsMessage.getStyleClass().add("custom-label");

        List<Ticket> tickets = getUserTickets(conn);

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");

        for (Ticket ticket : tickets) {
            Button ticketButton = new Button(ticket.getEvent().getEventName());
            ticketButton.setPrefSize(400, 50);
            ticketButton.setOnAction(e -> {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Ticket Sale");
                confirmationAlert.setHeaderText("Are you sure you want to sell this ticket?");
                confirmationAlert.setContentText("You will receive: " + ticket.getPrice()/2 + "$");

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    TicketService.getInstance().sellTicket(conn, ticket.getId(), getUser());
                    buttonMessage.setStyle("-fx-text-fill: green;");
                    buttonMessage.setText("Ticket sold successfully! Money added to your account: " + ticket.getPrice()/2 + "$");
                } else {
                    buttonMessage.setStyle("-fx-text-fill: yellow;");
                    buttonMessage.setText("Ticket sale canceled.");
                }

                // Reload the tickets
                delay(7, stage);
            });
            vbox.getChildren().add(ticketButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(ticketsMessage);
        BorderPane.setAlignment(ticketsMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(4, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Sell tickets");
        stage.setScene(scene);
    }

    // Event submenu
    private void showEvents(Stage stage) {
        // Create a welcome message
        Label eventsMessage = new Label("View tickets details");
        eventsMessage.getStyleClass().add("custom-label");

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Button footballButton = new Button("Football matches");
        footballButton.setPrefSize(200, 50);

        Button concertButton = new Button("Concerts");
        concertButton.setPrefSize(200, 50);

        Button ufcButton = new Button("Online UFC events");
        ufcButton.setPrefSize(200, 50);

        vbox.getChildren().addAll(footballButton, concertButton, ufcButton, backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(eventsMessage);
        BorderPane.setAlignment(eventsMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");
        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        footballButton.setOnAction(e -> {
            // Display football matches
            delay(9, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing football matches...");
        });

        concertButton.setOnAction(e -> {
            // Display concerts
            delay(10, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing concerts...");
        });

        ufcButton.setOnAction(e -> {
            // Display UFC events
            delay(11, stage);
            buttonMessage.setStyle("-fx-text-fill: green;");
            buttonMessage.setText("Viewing UFC events...");
        });

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(2, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("View tickets");
        stage.setScene(scene);
    }

    private void showFootballMatches(Stage stage) {
        // Create a welcome message
        Label footballMessage = new Label("Football matches");
        footballMessage.getStyleClass().add("custom-label");

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");

        for (Event event : events) {
            if(!(event instanceof FootballMatch)) {
                continue;
            }
            Button eventButton = new Button(event.getEventName());
            eventButton.setPrefSize(400, 50);
            eventButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Event Details");
                alert.setHeaderText("Details for: " + event.getEventName());
                alert.setContentText(event.toString());

                ButtonType purchaseButton = new ButtonType("Purchase");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(purchaseButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == purchaseButton) {
                    // Create a ChoiceDialog for bundle selection
                    List<String> bundleOptions = Arrays.asList("Standard", "Gold", "Gold+");
                    ChoiceDialog<String> bundleDialog = new ChoiceDialog<>(bundleOptions.get(0), bundleOptions);
                    bundleDialog.setTitle("Select Bundle");
                    bundleDialog.setHeaderText("Choose a bundle for the event: " + event.getEventName() +
                            "\nStandard: 500$\n" +
                            "Gold: 1500$\n" +
                            "Gold+: 2500$");
                    bundleDialog.setContentText("Available bundles:");

                    Optional<String> selectedBundle = bundleDialog.showAndWait();
                    AtomicBoolean sufficientFunds = new AtomicBoolean(false);
                    selectedBundle.ifPresent(bundle -> {
                        System.out.println("Selected bundle: " + bundle);
                        if(TicketService.getInstance().buyFootballTicket(getConn(), getUser(), (FootballMatch) event, bundle, ((FootballMatch) event).getSeatsAvailable())!=null)
                            sufficientFunds.set(true);
                        if (!sufficientFunds.get()) {
                            buttonMessage.setStyle("-fx-text-fill: red;");
                            buttonMessage.setText("Insufficient funds to purchase ticket for event: " + event.getEventName());
                            System.out.println("Insufficient funds to purchase ticket for event: " + event.getEventName());
                        }
                        else {
                            buttonMessage.setStyle("-fx-text-fill: green;");
                            buttonMessage.setText("Ticket purchased for event: " + event.getEventName());
                            System.out.println("Ticket purchased for event: " + event.getEventName());
                        }
                    });
                } else {
                    System.out.println("Purchase canceled.");
                    buttonMessage.setStyle("-fx-text-fill: yellow;");
                    buttonMessage.setText("Purchase canceled.");
                }
                });
            vbox.getChildren().add(eventButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(footballMessage);
        BorderPane.setAlignment(footballMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(8, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Transfer tickets");
        stage.setScene(scene);
    }

    private void showConcerts(Stage stage) {
        // Create a welcome message
        Label concertMessage = new Label("Concerts");
        concertMessage.getStyleClass().add("custom-label");

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");

        for (Event event : events) {
            if(!(event instanceof Concert)) {
                continue;
            }
            Button eventButton = new Button(event.getEventName());
            eventButton.setPrefSize(400, 50);
            eventButton.setOnAction(e -> {
                // Create a dialog to input the recipient's email
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Event Details");
                alert.setHeaderText("Details for: " + event.getEventName());
                alert.setContentText(event.toString());

                ButtonType purchaseButton = new ButtonType("Purchase");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(purchaseButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == purchaseButton) {
                    // Create a ChoiceDialog for bundle selection
                    List<String> rowOptions = Arrays.asList("A", "B", "C");
                    ChoiceDialog<String> rowDialog = new ChoiceDialog<>(rowOptions.get(0), rowOptions);
                    rowDialog.setTitle("Select Row");
                    rowDialog.setHeaderText("Choose your row for the event: " + event.getEventName() +
                            "\nA: 1000$\n" +
                            "B: 600$\n" +
                            "C: 200$");
                    rowDialog.setContentText("Available Rows:");

                    Optional<String> selectedRow = rowDialog.showAndWait();
                    AtomicBoolean sufficientFunds = new AtomicBoolean(false);
                    selectedRow.ifPresent(row -> {
                        System.out.println("Selected row: " + row);
                        if(TicketService.getInstance().buyConcertTicket(getConn(), getUser(), (Concert) event, row)!=null)
                            sufficientFunds.set(true);
                        if (!sufficientFunds.get()) {
                            buttonMessage.setStyle("-fx-text-fill: red;");
                            buttonMessage.setText("Insufficient funds to purchase ticket for event: " + event.getEventName());
                            System.out.println("Insufficient funds to purchase ticket for event: " + event.getEventName());
                        } else {
                            buttonMessage.setStyle("-fx-text-fill: green;");
                            buttonMessage.setText("Ticket purchased for event: " + event.getEventName());
                            System.out.println("Ticket purchased for event: " + event.getEventName());
                        }
                    });
                } else {
                    System.out.println("Purchase canceled.");
                    buttonMessage.setStyle("-fx-text-fill: yellow;");
                    buttonMessage.setText("Purchase canceled.");
                }
            });
            vbox.getChildren().add(eventButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(concertMessage);
        BorderPane.setAlignment(concertMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(8, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("Concerts");
        stage.setScene(scene);
    }

    private void showUFCOnline(Stage stage) {
        // Create a welcome message
        Label concertMessage = new Label("Concerts");
        concertMessage.getStyleClass().add("custom-label");

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 50);

        // Display the tickets
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        // Create a label for the exit message (initially hidden)
        Label buttonMessage = new Label();
        buttonMessage.getStyleClass().add("register-message-label");

        for (Event event : events) {
            if(!(event instanceof UFCOnline)) {
                continue;
            }
            Button eventButton = new Button(event.getEventName());
            eventButton.setPrefSize(400, 50);
            eventButton.setOnAction(e -> {
                // Create a dialog to input the recipient's email
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Event Details");
                alert.setHeaderText("Details for: " + event.getEventName() +
                        "\nPrices: Kickboxing: 2000$\n" +
                        "MMA: 3000$\n" +
                        "Taekwando: 4000$");
                alert.setContentText(event.toString());

                ButtonType purchaseButton = new ButtonType("Purchase");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(purchaseButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == purchaseButton){
                    String accessCode = UUID.randomUUID().toString();
                    AtomicBoolean sufficientFunds = new AtomicBoolean(false);
                    if(TicketService.getInstance().buyUFCOnlineTicket(getConn(), getUser(), (UFCOnline) event, accessCode)!=null)
                        sufficientFunds.set(true);
                    if (!sufficientFunds.get()) {
                        buttonMessage.setStyle("-fx-text-fill: red;");
                        buttonMessage.setText("Insufficient funds to purchase ticket for event: " + event.getEventName());
                        System.out.println("Insufficient funds to purchase ticket for event: " + event.getEventName());
                    } else {
                        buttonMessage.setStyle("-fx-text-fill: green;");
                        buttonMessage.setText("Ticket purchased for event: " + event.getEventName());}

                } else {
                    System.out.println("Purchase canceled.");
                    buttonMessage.setStyle("-fx-text-fill: yellow;");
                    buttonMessage.setText("Purchase canceled.");
                }
            });
            vbox.getChildren().add(eventButton);
        }
        vbox.getChildren().add(backButton);

        // Create a BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(concertMessage);
        BorderPane.setAlignment(concertMessage, Pos.CENTER);
        borderPane.setCenter(vbox);

        // Create a StackPane to overlay the exit message
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        stackPane.setStyle("-fx-background-color: #423f3f;"); // Set background color

        StackPane.setAlignment(buttonMessage, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(buttonMessage);

        backButton.setOnAction(e -> {
            // Display the exit message
            delay(8, stage);
            buttonMessage.setStyle("-fx-text-fill: yellow;");
            buttonMessage.setText("Returning...");
        });

        // Set the scene
        Scene scene = new Scene(stackPane, 1280, 720);
        scene.getStylesheets().add("styles.css");
        stage.setTitle("UFC Matches");
        stage.setScene(scene);
    }

    private void loadUsers(Connection conn){
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("user_type");
                if(type.equals("Customer")){
                    users.put(rs.getString("email"), new Customer(
                            rs.getInt("id"),
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
            // Sort events by date
            Collections.sort(events);

        } catch (SQLException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    private List<Ticket> getUserTickets(Connection conn) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, getUser().getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int ticketId = rs.getInt("id");
                int eventId = rs.getInt("event_id");
                String type = rs.getString("type");
                String row = rs.getString("row");
                String bundle = rs.getString("bundle");
                int seat = rs.getInt("seat_number");
                String accessCode = rs.getString("access_code");

                Event event = null;
                for(Event e : events) {
                    if (e.getEventId() == eventId) {
                        event = e;
                        break;
                    }
                }
                Ticket ticket;

                // Instantiate the correct subclass based on the type
                if ("Concert".equals(type)) {
                    ticket = new ConcertTicket((Concert) event, getUser(), row, ticketId);
                } else if ("FootballMatch".equals(type)) {
                    ticket = new FootballTicket((FootballMatch) event, getUser(), bundle, seat, ticketId);
                } else if ("UFCOnline".equals(type)) {
                    ticket = new UFCOnlineTicket((UFCOnline) event, getUser(), accessCode, ticketId);
                } else {
                    continue; // Skip unknown ticket types
                }

                tickets.add(ticket);
            }
            Collections.sort(tickets);
        } catch (SQLException e) {
            System.out.println("Error loading tickets: " + e.getMessage());
        }
        return tickets;
    }

    private void delay(int type, Stage stage) {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.1));
        delay.setOnFinished(event -> {
            if(type == 0) {
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
            else if(type == 1) {
                start(stage);
            }
            else if(type == 2) {
                showUserMenu(getUser(), stage);
            }
            else if(type == 3) {
                showProfile(stage);
            }
            else if(type == 4) {
                showTicketsMenu(stage);
            }
            else if(type == 5) {
                showTickets(stage);
            }
            else if(type == 6) {
                transferTicket(stage);
            }
            else if(type == 7) {
                sellTickets(stage);
            }
            else if(type == 8) {
                showEvents(stage);
            }
            else if(type == 9) {
                showFootballMatches(stage);
            }
            else if(type == 10) {
                showConcerts(stage);
            }
            else if(type == 11) {
                showUFCOnline(stage);
            }
            else if(type == 12)
            {
                showEditProfile(getUser(), stage);
            }
        });
        delay.play();
    }

    public static Connection getConn() {
        return conn;
    }

    private static void setUser(User user) {
        Menu.user = user;
    }

    private static User getUser() {
        return user;
    }

}
