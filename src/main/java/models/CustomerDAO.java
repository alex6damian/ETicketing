package models;

import utils.PasswordUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// Data Acces Object
public class CustomerDAO extends UserDAO {

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (id, name, password, email, address, phone, user_type, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, PasswordUtils.hashPassword(user.getPassword()));
            stmt.setString(4, user.getEmail());
            stmt.setString(5, ((Customer) user).getAddress());
            stmt.setString(6, ((Customer) user).getPhoneNumber());
            stmt.setString(7, user.getUserType());  // "Customer"
            stmt.setDouble(8, user.getBalance());

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