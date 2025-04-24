package models;

public class UFCOnlineTicket extends Ticket<UFCOnline> {
    protected String accesCode;

    public UFCOnlineTicket(UFCOnline event, User user, String accesCode) {
        super(1000, event, user);
        this.accesCode = accesCode;
    }

    // Constructor for loading from database
    public UFCOnlineTicket(UFCOnline event, User user, String accesCode, int id) {
        super(1000, event, user, id);
        this.accesCode = accesCode;
    }

    @Override
    public double calculatePrice() {
        return price;
    }

    @Override
    public String toString() {
        super.toString();
        return ", accesCode: " + accesCode + ", price: " + price;
    }

}
