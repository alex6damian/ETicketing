package models;

public class ConcertTicket extends Ticket<Concert> {
    protected String row;

    public ConcertTicket(Concert concert, User user, String row) {
        super(200, concert, user);
        this.row = row;
        this.setPrice(this.calculatePrice());
    }

    // Constructor for loading from database
    public ConcertTicket(Concert concert, User user, String row, int id) {
        super(200, concert, user, id);
        this.row = row;
    }

    @Override
    public double calculatePrice() {
        switch (row) {
            case "A":
                return price * 5;
            case "B":
                return price * 3;
            case "C":
                return price;
            default:
                System.out.println("Invalid bundle");
        }
        return price;
    }
    @Override
    public String toString() {
        return super.toString() + ", row: " + row + ", price: " + price;
    }
}
