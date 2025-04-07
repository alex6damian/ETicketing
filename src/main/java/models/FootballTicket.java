package models;

public class FootballTicket extends Ticket<FootballMatch> {
    protected String bundle;
    protected int seat;

    protected FootballTicket(float price, FootballMatch event, String bundle, int seat) {
        super(price, event);
        this.bundle = bundle;
        this.seat = seat;
        this.setPrice(this.getPrice());
    }

    @Override
    public float getPrice() {
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
        super.toString();
        return ", bundle: " + bundle + ", seat: " + seat + ", price: " + price;
    }
}
