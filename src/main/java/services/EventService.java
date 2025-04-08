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
}