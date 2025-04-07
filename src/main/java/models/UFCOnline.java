package models;

public class UFCOnline extends Event {
    protected String link;
    protected String fightType;

    UFCOnline(String date, String time, String location, String description, String eventName, String link) {
        super(date, time, location, description, eventName);
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "UFC Online: " + link + ", Fight type:" + fightType;
    }

    @Override
    public String getEventType() {
        return "UFCOnline";
    }
}
