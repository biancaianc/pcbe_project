package common.models;

import server.CountdownThread;

public class Client {
    private String name;
    public Client(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }

    private CountdownThread pingCheckerThread;

    public void die(){
        pingCheckerThread.interrupt();
//        messagingThread.interrupt();

    }
}
