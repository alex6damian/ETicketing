package models;

public class UFCOnlineTicket extends Ticket<UFCOnline> {
    protected String accesCode;

    public UFCOnlineTicket(float price, UFCOnline event, User user, String accesCode) {
        super(price, event, user);
        this.accesCode = accesCode;
    }
    

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        super.toString();
        return ", accesCode: " + accesCode + ", price: " + price;
    }

}
