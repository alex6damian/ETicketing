package models;

public class FootballTicket extends Ticket<FootballMatch> {
    protected String bundle;
    protected int seat;

    public FootballTicket(FootballMatch event, User user, String bundle, int seat) {
        super(500, event, user);
        this.bundle = bundle;
        this.seat = seat;
        this.setPrice(calculatePrice());
    }

    // Constructor for loading from database
    public FootballTicket(FootballMatch event, User user, String bundle, int seat, int id) {
        super(500, event, user, id);
        this.bundle = bundle;
        this.seat = seat;
        this.setPrice(calculatePrice());
    }

    @Override
    public double calculatePrice() {
        switch (bundle) {
            case "Standard":
                return price;
            case "Gold":
                return price * 3;
            case "Gold+":
                return price * 5;
            default:
                System.out.println("Invalid bundle");
        }
        return price;
    }

    @Override
    public String toString() {
        return super.toString() + "\nBundle: " + bundle +
                "\nSeat number: " + seat +
                "\nPrice: " + price + "$";
    }
}
