package models;

public class ConcertTicket extends Ticket<Concert> {
    protected String row;

    public ConcertTicket(Concert concert, User user, String row) {
        super(200, concert, user);
        this.row = row;
        this.setPrice(this.calculatePrice());
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
