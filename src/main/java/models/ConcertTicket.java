package models;

public class ConcertTicket extends Ticket<Concert> {
    protected String row;

    ConcertTicket(float price, Concert concert, User user, String row) {
        super(price, concert, user);
        this.row = row;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return super.toString() + ", row: " + row + ", price: " + price;
    }
}
