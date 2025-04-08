package services;

import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketService {

    public FootballTicket buyFootballTicket(Connection conn, User user, FootballMatch event, String bundle, int seat) {
        FootballTicket ticket = new FootballTicket(event, user, bundle, seat);
        String insertQuery = "INSERT INTO tickets (id, user_id, event_id, price, type, bundle, seat_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM tickets WHERE user_id = ? AND event_id = ? AND seat_number = ?";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            float price = ticket.getPrice();
            ticket.setPrice(price);

            System.out.println(ticket.getId());

            // Insert the ticket into the database
            insertStmt.setInt(1, ticket.getId());
            insertStmt.setInt(2, user.getId());
            insertStmt.setInt(3, event.getEventId());
            insertStmt.setFloat(4, price);
            insertStmt.setString(5, "FootballMatch");
            insertStmt.setString(6, bundle);
            insertStmt.setInt(7, seat);
            insertStmt.executeUpdate();

            // Retrieve the generated ticket ID
            selectStmt.setInt(1, user.getId());
            selectStmt.setInt(2, event.getEventId());
            selectStmt.setInt(3, seat);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int ticketId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticket;
    }
}