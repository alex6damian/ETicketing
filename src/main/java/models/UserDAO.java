package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static boolean updateUserProfile(Connection conn, User user, String name, String email, String address, String phone, String password) {
        try {
            if (!email.equals(user.getEmail())) {
                if (isEmailInUse(conn, email, user.getId())) {
                    return false;
                }
            }

            if (phone != null && !phone.equals(((Customer) user).getPhoneNumber())) {
                if (isPhoneInUse(conn, phone, user.getId())) {
                    return false;
                }
            }

            StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
            List<Object> parameters = new ArrayList<>();
            boolean hasChanges = false;

            if (!name.equals(user.getName())) {
                queryBuilder.append("name = ?, ");
                parameters.add(name);
                hasChanges = true;
            }

            if (!email.equals(user.getEmail())) {
                queryBuilder.append("email = ?, ");
                parameters.add(email);
                hasChanges = true;
            }

            // Fix here: compare address with the user's address, not email
            if (!address.equals(((Customer) user).getAddress())) {
                queryBuilder.append("address = ?, ");
                parameters.add(address);
                hasChanges = true;
            }

            if (phone != null && !phone.equals(((Customer) user).getPhoneNumber())) {
                queryBuilder.append("phone = ?, ");
                parameters.add(phone);
                hasChanges = true;
            }

            if (password != null && !password.isEmpty()) {
                String hashedPassword = PasswordUtils.hashPassword(password);
                queryBuilder.append("password = ?, ");
                parameters.add(hashedPassword);
                hasChanges = true;
            }

            if (!hasChanges) {
                System.out.println("No changes to update");
                return true;
            }

            String updateQuery = queryBuilder.substring(0, queryBuilder.length() - 2) + " WHERE id = ?";
            parameters.add(user.getId());

            System.out.println(updateQuery);

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                System.out.println("Executing update: " + stmt.toString());
                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace(); // Adding stack trace for better debugging
            return false;
        }
    }

    private static boolean isEmailInUse(Connection conn, String email, int excludeUserId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, email);
            stmt.setInt(2, excludeUserId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static boolean isPhoneInUse(Connection conn, String phone, int excludeUserId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE phone = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, phone);
            stmt.setInt(2, excludeUserId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public abstract boolean deleteUser(int userId);

}

