package models;

abstract class Event {
    String date;
    String time;
    String location;
    String description;
    String eventName;
    float price;
    int eventId;
    static int eventCount;

    Event(String date, String time, String location, String description, String eventName) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.eventName = eventName;
        this.eventId = eventCount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getEventName() {
        return eventName;
    }

    public int getEventId() {
        return eventId;
    }

    public float getPrice() {
        return price;
    }

    // Static and non-static blocks
    static {
        eventCount = 0;
    }
    {
        eventCount++;
    }
}

class Concert extends Event {
    String artist;
    String genre;
    int seatsAvailable;

    Concert(String date, String time, String location, String description, String eventName, String artist, String genre, int seatsAvailable) {
        super(date, time, location, description, eventName);
        this.artist = artist;
        this.genre = genre;
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public float getPrice() {
        if (seatsAvailable == 0)
            return 0;
        return price * (seatsAvailable <= 10 ? 1.5f : 1.0f);
    }

    public void sellTicket(int numberOfTickets) {
        if (numberOfTickets <= seatsAvailable) {
            seatsAvailable -= numberOfTickets;
            System.out.println("Tickets sold: " + numberOfTickets);
        } else {
            System.out.println("Not enough tickets available.");
        }
    }

    @Override
    public String toString() {
        return "Concert{" +
                "artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}