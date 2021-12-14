package server;

import common.models.ClientModel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CountdownThread extends Thread {
    private final ConcurrentLinkedQueue<ClientModel> clients;

    public CountdownThread(ConcurrentLinkedQueue<ClientModel> clients) {
        this.clients=clients;
    }

    public void run(){
        while(true){
            Iterator<ClientModel> iterator = clients.iterator();

            while (iterator.hasNext()) {
                ClientModel client=iterator.next();
                if (!client.checkLiveliness()) {
                    System.out.println(client.toString() + " was removed due to inactivity.");
                    clients.remove(client);
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
