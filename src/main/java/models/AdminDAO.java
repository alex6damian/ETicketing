package models;

import utils.PasswordUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// Data Acces Object
public class AdminDAO extends UserDAO {

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (id, name, password, email, role, user_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, PasswordUtils.hashPassword(user.getPassword()));
            stmt.setString(4, user.getEmail());
            stmt.setString(5, ((Admin) user).getRole());
            stmt.setString(6, user.getUserType());  // "Customer"

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        // Implementare pentru actualizarea unui Customer
        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        // Implementare pentru ștergerea unui Customer
        return false;
    }
}
