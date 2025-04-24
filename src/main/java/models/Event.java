package models;

public abstract class Event implements Comparable<Event> {
    int eventId;
    String date;
    String time;
    String location;
    String description;
    String eventName;
    static int eventCount;


    Event(int eventId, String date, String time, String location, String description, String eventName) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.eventName = eventName;
        this.eventId = eventId;
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
//    static {
//        eventCount = 0;
//    }
//    {
//        eventCount++;
//    }`

    @Override
    public int compareTo(Event obj) {
        return this.date.compareTo(obj.date);
    }

    public String toString() {
        return "\nEvent name: " + this.getEventName() +
                "\nDate: " + this.getDate() +
                "\nTime: " + this.getTime() +
                "\nLocation: " + this.getLocation() +
                "\nDescription: " + this.getDescription();
        }

}
