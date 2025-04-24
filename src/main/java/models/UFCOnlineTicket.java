package models;

public class UFCOnlineTicket extends Ticket<UFCOnline> {
    protected String accesCode;

    public UFCOnlineTicket(UFCOnline event, User user, String accesCode) {
        super(1000, event, user);
        this.accesCode = accesCode;
        this.setPrice(this.calculatePrice());
    }

    // Constructor for loading from database
    public UFCOnlineTicket(UFCOnline event, User user, String accesCode, int id) {
        super(1000, event, user, id);
        this.accesCode = accesCode;
        this.setPrice(this.calculatePrice());
    }

    @Override
    public double calculatePrice() {
        switch (event.getFightType())
        {
            case "Kickboxing":
                return price * 2;
            case "MMA":
                return price * 3;
            case "Taekwondo":
                return price * 4;
            default:
                System.out.println("Invalid fight type");
        }
        return price;
    }

    @Override
    public String toString() {
        super.toString();
        return ", accesCode: " + accesCode + ", price: " + price;
    }

}
