package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventService {
    private static EventService instance;

    private EventService() {}

    public static EventService getInstance() {
        if (instance == null) {
            synchronized (EventService.class) {
                if (instance == null) {
                    instance = new EventService();
                }
            }
        }
        return instance;
    }

    // Method to update the number of available seats for an event
    public void updateSeatsAvailable(Connection conn, int eventId, int seatsAvailable) {
        String updateQuery = "UPDATE Event SET seatsAvailable = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, seatsAvailable); // Setează locurile disponibile
            stmt.setInt(2, eventId); // Setează ID-ul evenimentului
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Seats available updated successfully for event ID: " + eventId);
            } else {
                System.out.println("No rows updated. Check if the event ID exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating seats available: " + e.getMessage());
        }
    }

    // Method to update the event price
    public void updateEventPrice(Connection conn, int eventId, double newPrice) {
        String updateQuery = "UPDATE Event SET price = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, newPrice); // Set the new price
            stmt.setInt(2, eventId); // Set the event ID
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Event price updated successfully for event ID: " + eventId);
            } else {
                System.out.println("No rows updated. Check if the event ID exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating event price: " + e.getMessage());
        }
    }

    // Method to update the event date
    public void updateEventDate(Connection conn, int eventId, String newDate) {
        String updateQuery = "UPDATE Event SET date = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, newDate); // Set the new date
            stmt.setInt(2, eventId); // Set the event ID
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Event date updated successfully for event ID: " + eventId);
            } else {
                System.out.println("No rows updated. Check if the event ID exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating event date: " + e.getMessage());
        }
    }
}