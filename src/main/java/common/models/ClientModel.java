package common.models;

import server.ConnectionThread;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

import static server.ServerApplication.clients;

public class ClientModel {
    private String name;
    private String address;
    private ConnectionThread  messagingThread;
    private Timestamp lastPing;

    public ClientModel(Socket communicationSocket, int generalConsumerGroup){
        lastPing = new Timestamp(System.currentTimeMillis());
        address = communicationSocket.getInetAddress().toString();
        System.out.println("Client with ip "+address+" joined.");
        messagingThread = new ConnectionThread(communicationSocket, this);
        messagingThread.start();
        messagingThread.send("Your general thread consumer group id is: " + String.valueOf(generalConsumerGroup));

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getName(){
        return name;
    }

    public ConnectionThread getMessagingThread(){
        return messagingThread;
    }

    public void getPinged(){
        //TODO log recieved pings
        this.lastPing = new Timestamp(System.currentTimeMillis());

    }

    @Override
    public String toString(){
        if(name==null)
            return "client at address "+address;
        else
            return "client "+name;
    }

    public boolean checkLiveliness() {
        return System.currentTimeMillis() - lastPing.getTime() < 1500;

    }
}
