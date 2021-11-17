package common.models;

import server.ConnectionThread;
import server.CountdownThread;

import java.net.Socket;

public class Client {
    private String name;
    private ConnectionThread  messagingThread;
    private CountdownThread pingCheckerThread;
    public Client(String name){
        this.name=name;
    }
    public Client(Socket communicationSocket){
        messagingThread = new ConnectionThread(communicationSocket);
        pingCheckerThread = new CountdownThread(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void die(){
        pingCheckerThread.interrupt();
//        messagingThread.interrupt();

    }
}
