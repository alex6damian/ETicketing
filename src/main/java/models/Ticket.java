package models;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static models.UserDAO.connection;

public abstract class Ticket<T extends Event> {
    protected final int id;
    protected float price;
    protected T event;
    protected User user;
    protected static int counter = 0;
    protected Connection conn;

    protected Ticket(float price, T event, User user) {
        this.id = counter;
        this.price = price;
        this.event = event;
        this.user = user;
    }

    static
    {
        counter = getTicketCount();
    }

    {
        counter++;
    }

    public int getId() {
        return id;
    }


    public abstract float getPrice();
    public void setPrice(float price) {
        this.price = price;
    }

    public T getEvent() {
        return event;
    }
    public void setEvent(T event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static int getTicketCount() {
        String query = "SELECT COUNT(*) FROM tickets";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Ticket count from database: " + count); // Debugging log
                return count;
            }
        } catch (Exception e) {
            System.err.println("Error getting ticket count: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", price=" + price + ", event=" + event + "]";
    }
}

