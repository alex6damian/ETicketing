package models;

public abstract class Ticket<T extends Event> {
    protected final int id;
    protected float price;
    protected T event;
    protected static int counter = 0;

    protected Ticket( float price, T event) {
        this.id = counter;
        this.price = price;
        this.event = event;
    }

    static
    {

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

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", price=" + price + ", event=" + event + "]";
    }
}

