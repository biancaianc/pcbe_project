package models;

import java.util.Date;

public class Message {
    private String message;
    private Client sender;
    private Date date;
    private String type;
    private Room room;


    public Message(String message, Client sender, Date date, String type, Room room) {
        this.message = message;
        this.sender = sender;
        this.date = date;
        this.type = type;
        this.room = room;
    }

    public String getMessage() {
        return message;
    }

    public Client getSender() {
        return sender;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public Room getRoom() {
        return room;
    }
}
