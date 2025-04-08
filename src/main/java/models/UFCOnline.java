package models;

public class UFCOnline extends Event {
    protected String link;
    protected String fightType;

    public UFCOnline(int eventId, String date, String time, String location, String description, String eventName, String link, String fightType) {
        super(eventId, date, time, location, description, eventName);
        this.link = link;
        this.fightType = fightType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFightType() {
        return fightType;
    }

    public void setFightType(String fightType) {
        this.fightType = fightType;
    }

    @Override
    public String toString() {

        return super.toString() + "\nFight type: " + fightType;
    }

    @Override
    public String getEventType() {
        return "UFCOnline";
    }
}
