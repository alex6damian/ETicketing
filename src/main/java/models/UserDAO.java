package models;
import java.sql.Connection;
import utils.DatabaseConnection;

public abstract class UserDAO {
    protected Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public abstract boolean addUser(User user);
    public abstract boolean updateUser(User user);
    public abstract boolean deleteUser(int userId);
}
