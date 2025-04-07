package models;

public class Concert extends Event {
    String artist;
    String genre;
    int seatsAvailable;

    public Concert(String date, String time, String location, String description, String eventName, String artist, String genre, int seatsAvailable) {
        super(date, time, location, description, eventName);
        this.artist = artist;
        this.genre = genre;
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
        return "Concert, artist: " + artist + ", genre: " + genre + ", seatsAvailable: " + seatsAvailable + "\n";
    }

    @Override
    public String getEventType() {
        return "Concert";
        }
    }

