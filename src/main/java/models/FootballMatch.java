package models;

public class FootballMatch extends Event {
    String stadiumName;
    int seatsAvailable;


    public FootballMatch(String date, String time, String location, String description, String eventName, String stadiumName, int seatsAvailable) {
        super(date, time, location, description, eventName);
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
    }

    @Override
    public String toString() {
        return super.toString() + "\nStadium name: " + stadiumName +
                ", Seats available: " + seatsAvailable ;
    }

    @Override
    public String getEventType() {
        return "FootballMatch";
    }

}
