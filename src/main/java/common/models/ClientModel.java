package common.models;

import server.ConnectionThread;
import server.CountdownThread;

import java.net.Socket;

import static server.ServerApplication.clients;

public class ClientModel {
    private String name;
    private String address;
    private ConnectionThread  messagingThread;
    private CountdownThread pingCheckerThread;

    public ClientModel(Socket communicationSocket){
        address = communicationSocket.getInetAddress().toString();
        System.out.println("Client with ip "+address+" joined.");
        messagingThread = new ConnectionThread(communicationSocket, this);
        messagingThread.start();
        pingCheckerThread = new CountdownThread(this);
        pingCheckerThread.start();
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

    public void getPinged(){
        //TODO log recieved pings
        pingCheckerThread.getPinged();
    }

    public void die(){
        pingCheckerThread.interrupt();
        messagingThread.interrupt();
        System.out.println( toString() + " stopped sending pings.");
        clients.remove(this);
    }

    @Override
    public String toString(){
        if(name==null)
            return "client at address "+address;
        else
            return "client "+name;
    }
}
