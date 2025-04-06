import models.Customer;
import models.CustomerDAO;
import models.UserDAO;
import ui.Menu;
import utils.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        if (conn != null) {
            System.out.println("Database connection is working.");
        } else {
            System.out.println("Database connection failed.");
            return;
        }
        
        Menu menu = new Menu();
        menu.run();
    }
}