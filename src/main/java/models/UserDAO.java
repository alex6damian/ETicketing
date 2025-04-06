package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import utils.DatabaseConnection;
import utils.PasswordUtils;

public abstract class UserDAO {
    protected static Connection connection;

    public UserDAO() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public int getUserCount() {
        String query = "SELECT COUNT(*) FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error getting user count: " + e.getMessage());
        }
        return 0;
    }

    public abstract boolean addUser(User user);

    public abstract boolean updateUser(User user);

    public abstract boolean deleteUser(int userId);


}
