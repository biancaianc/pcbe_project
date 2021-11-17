package server;

import common.models.Client;

public class CountdownThread extends Thread {
    private boolean gotPing;
    private Client client;

    public CountdownThread(Client client) {
        this.client=client;
    }

    public void run(){
        do {
            try {
                gotPing = false;
                sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(gotPing);
        client.die();
    }

    public void getPing(){
        gotPing = true;
    }
}
