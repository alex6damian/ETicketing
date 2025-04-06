package ui;

import java.util.Objects;
import java.util.Scanner;

public class Menu {
    private static boolean userLoggedIn;
    private static Menu instance;
    private Scanner Scanner;

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

    public void run(){
        int option;
        do{
            System.out.println("\n=== E-Ticketing Menu ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            option = Scanner.nextInt();
            Scanner.nextLine(); // Consume newline

            switch(option){
                case 1:
                    System.out.println("Registering...");
                    // Call register method
                    break;
                case 2:
                    userLoggedIn = this.login();
                    if(userLoggedIn){
                        System.out.println("Logging in...");
                        this.userMenu();
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

    private boolean login(){
        System.out.print("Enter email: ");
        String email = Scanner.nextLine();
        System.out.print("Enter password: ");
        String password = Scanner.nextLine();

        return Objects.equals(email, "alex6damian@gmail.com") && Objects.equals(password, "123456");
    }

    private void userMenu(){
        int option;
        do{
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Logout");
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
                    userLoggedIn = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while(userLoggedIn);
    }
}
