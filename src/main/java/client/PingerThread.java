package client;

import common.models.ServerModel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static common.ConnectionConstants.pingTimeout;
import static common.ConnectionConstants.pingingPort;

public class PingerThread extends Thread {
    private ServerModel server;
    public PingerThread(ServerModel server) {
        this.server=server;
    }

    @Override
    public void run(){
        int fails = 0;
        while(fails<2) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try  {
                new Socket(server.getAddress(),pingingPort);
                //TODO log ping sent
                fails=0;
            } catch (IOException e) {
                //TODO log ping could not be sent
                fails++;
            }
        }
        System.out.println("Pinging connection to server lost.");
    }

}
