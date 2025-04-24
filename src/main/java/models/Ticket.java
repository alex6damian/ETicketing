package models;

import ui.Menu;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static models.UserDAO.connection;

public abstract class Ticket<T extends Event> implements Comparable<Ticket<T>> {
    protected final int id;
    protected double price;
    protected T event;
    protected User user;
    protected static int counter;

    protected Ticket(double price, T event, User user) {
        this.id = counter;
        this.price = price;
        this.event = event;
        this.user = user;
    }

    protected Ticket(double price, T event, User user, int id) {
        this.id = id;
        this.price = price;
        this.event = event;
        this.user = user;
        counter--;
    }

    static
    {
        counter = getTicketCount();
        System.out.println("Ticket count: " + counter); // Debugging log
    }

    {
        counter++;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract double calculatePrice();

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
        try (PreparedStatement stmt = Menu.getConn().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // System.out.println("Ticket count from database: " + count); // Debugging log
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error getting ticket count: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
        }
        return 0;
    }

    @Override
    public String toString() {
        return event.toString();
    }

    @Override
    public int compareTo(Ticket<T> other) {
        return Double.compare(this.price, other.price);
    }
}

