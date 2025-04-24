package services;

import models.*;
import ui.Menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketService {
    private static TicketService instance;

    private TicketService() {}

    public static TicketService getInstance() {
        if (instance == null) {
            synchronized (TicketService.class) {
                if (instance == null) {
                    instance = new TicketService();
                }
            }
        }
        return instance;
    }

    // Method to buy a football ticket
    public FootballTicket buyFootballTicket(Connection conn, User user, FootballMatch event, String bundle, int seat) {
        FootballTicket ticket = new FootballTicket(event, user, bundle, seat);
        String insertQuery = "INSERT INTO tickets (id, user_id, event_id, price, type, bundle, seat_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM tickets WHERE user_id = ? AND event_id = ? AND seat_number = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            double price = ticket.calculatePrice();
            ticket.setPrice(price);

            // Insert the ticket into the database
            insertStmt.setInt(1, ticket.getId());
            insertStmt.setInt(2, user.getId());
            insertStmt.setInt(3, event.getEventId());
            insertStmt.setDouble(4, price);
            insertStmt.setString(5, "FootballMatch");
            insertStmt.setString(6, bundle);
            insertStmt.setInt(7, seat);
            insertStmt.executeUpdate();

            // Retrieve the generated ticket ID
            selectStmt.setInt(1, user.getId());
            selectStmt.setInt(2, event.getEventId());
            selectStmt.setInt(3, seat);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the user's balance
        updateBalance(user, ticket.getPrice());
        return ticket;
    }

    // Method to buy a concert ticket
    public ConcertTicket buyConcertTicket(Connection conn, User user, Concert event, String row) {
        ConcertTicket ticket = new ConcertTicket(event, user, row);
        String insertQuery = "INSERT INTO tickets (id, user_id, event_id, price, type, row) VALUES (?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM tickets WHERE user_id = ? AND event_id = ? AND row = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            double price = ticket.calculatePrice();
            ticket.setPrice(price);

            // Insert the ticket into the database
            insertStmt.setInt(1, ticket.getId());
            insertStmt.setInt(2, user.getId());
            insertStmt.setInt(3, event.getEventId());
            insertStmt.setDouble(4, price);
            insertStmt.setString(5, "Concert");
            insertStmt.setString(6, row);
            insertStmt.executeUpdate();

            // Retrieve the generated ticket ID
            selectStmt.setInt(1, user.getId());
            selectStmt.setInt(2, event.getEventId());
            selectStmt.setString(3, row);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the user's balance
        updateBalance(user, ticket.getPrice());
        return ticket;
    }

    // Method to buy a UFC online ticket
    public UFCOnlineTicket buyUFCOnlineTicket(Connection conn, User user, UFCOnline event, String accessCode) {
        UFCOnlineTicket ticket = new UFCOnlineTicket(event, user, accessCode);
        String insertQuery = "INSERT INTO tickets (id, user_id, event_id, price, type, access_code) VALUES (?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM tickets WHERE user_id = ? AND event_id = ? AND access_code = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            double price = ticket.calculatePrice();
            ticket.setPrice(price);

            // Insert the ticket into the database
            insertStmt.setInt(1, ticket.getId());
            insertStmt.setInt(2, user.getId());
            insertStmt.setInt(3, event.getEventId());
            insertStmt.setDouble(4, price);
            insertStmt.setString(5, "UFCOnline");
            insertStmt.setString(6, accessCode);
            insertStmt.executeUpdate();

            // Retrieve the generated ticket ID
            selectStmt.setInt(1, user.getId());
            selectStmt.setInt(2, event.getEventId());
            selectStmt.setString(3, accessCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateBalance(user, ticket.getPrice());
        return ticket;
    }

    // Method to transfer a ticket
    public static void transferTicket(Connection conn, int ticketId, User newUser) {
        String updateQuery = "UPDATE tickets SET user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, newUser.getId());
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
            System.out.println("Ticket transferred successfully.");
        } catch (SQLException e) {
            System.err.println("Error transferring ticket: " + e.getMessage());
        }
    }

    // Method to sell a ticket
    public void sellTicket(Connection conn, int ticketId, User user) {
        String selectQuery = "SELECT price FROM tickets WHERE id = ? AND user_id = ?";
        String deleteQuery = "DELETE FROM tickets WHERE id = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Validate ticket ownership and retrieve price
            selectStmt.setInt(1, ticketId);
            selectStmt.setInt(2, user.getId());
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                double ticketPrice = rs.getDouble("price");

                // Delete the ticket
                deleteStmt.setInt(1, ticketId);
                deleteStmt.executeUpdate();

                updateBalance(user, -ticketPrice); // Deduct the ticket price from the user's balance

                System.out.println("Ticket sold successfully. Balance updated.");
            } else {
                System.out.println("Ticket not found or does not belong to the user.");
            }
        } catch (SQLException e) {
            System.err.println("Error selling ticket: " + e.getMessage());
        }
    }

    // Method to update the user's balance
    private void updateBalance(User user, double amount) {
        String updateQuery = "UPDATE users SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = Menu.getConn().prepareStatement(updateQuery)) {
            stmt.setDouble(1, user.getBalance() - amount);
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}