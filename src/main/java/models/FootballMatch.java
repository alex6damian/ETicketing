package models;

import services.EventService;
import ui.Menu;

public class FootballMatch extends Event {
    String stadiumName;
    int seatsAvailable;


    public FootballMatch(int eventId, String date, String time, String location, String description, String eventName, String stadiumName, int seatsAvailable) {
        super(eventId, date, time, location, description, eventName);
        this.stadiumName = stadiumName;
        this.seatsAvailable = seatsAvailable;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public void sellTicket(int numberOfTickets) {
        if (numberOfTickets <= seatsAvailable) {
            seatsAvailable -= numberOfTickets;
            System.out.println("Tickets sold: " + numberOfTickets);
        } else {
            System.out.println("Not enough tickets available.");
        }

        EventService.getInstance().updateSeatsAvailable(Menu.getConn(), this.getEventId(), this.getSeatsAvailable());
    }

    @Override
    public String toString() {
        return super.toString() + "\nStadium name: " + stadiumName +
                "\nSeats available: " + seatsAvailable ;
    }

    @Override
    public String getEventType() {
        return "FootballMatch";
    }

}
