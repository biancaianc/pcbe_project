package server;

import common.models.ClientModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

import static common.ConnectionConstants.pingingPort;

public class PingReceiverThread extends Thread{

    @Override
    public void run(){
        try {
            ServerSocket pingSocket = new ServerSocket(pingingPort);
            while(true)
            {
                Socket socket = pingSocket.accept();
                String ip = socket.getInetAddress().toString();
                //TODO debug as INFO : recieved ping from <ip>
                ServerApplication.clients.stream().filter((clt)->clt.getAddress().equals(ip)).forEach(ClientModel::getPinged);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("pinging socket died.");
        }

    }
}
