package models;

public abstract class Event {
    int eventId;
    String date;
    String time;
    String location;
    String description;
    String eventName;
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

    public void sellTicket(int numberOfTickets){
    }

    public abstract String getEventType();

    // Static and non-static blocks
    static {
        eventCount = 0;
    }
    {
        eventCount++;
    }

    public String toString() {
        return "\nEvent id: " + this.getEventId() +
                "\nEvent name: " + this.getEventName() +
                ", Date: " + this.getDate() +
                ", Time: " + this.getTime() +
                ", Location: " + this.getLocation() +
                ", Description: " + this.getDescription();
        }
    }
