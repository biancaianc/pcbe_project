package server;

import common.models.ClientModel;

public class CountdownThread extends Thread {
    private boolean gotPing;
    private ClientModel clientModel;

    public CountdownThread(ClientModel clientModel) {
        this.clientModel = clientModel;
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
        clientModel.die();
    }

    public void getPinged(){
        gotPing = true;
    }
}
