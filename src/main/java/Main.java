import models.Customer;
import models.CustomerDAO;
import models.UserDAO;
import ui.Menu;
import utils.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Menu menu = new Menu();
        menu.run();
    }
}