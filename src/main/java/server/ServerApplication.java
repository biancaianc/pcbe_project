package server;

import common.models.ClientModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static common.ConnectionConstants.communicationPort;

public class ServerApplication {

    public static List<ClientModel> clients = new ArrayList<>();


    public static void main(String[] args) {

        PingRecieverThread pingReciever = new PingRecieverThread();
        pingReciever.start();

        System.out.println("Server is ready to accept clients.");

        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(communicationPort);

            while(true)
            {
                    Socket clientSocket = serverSocket.accept();
                    //UNCOMMENT THESE LINES TO MAKE CLIENTS ONLY WORK ON SEPARATE MACHINES. COMMENTED THEM FOR DEBUGGING.
//                    if(!clientExists(clientSocket.getInetAddress().toString()))
                        clients.add(new ClientModel(clientSocket));
//                    else
//                        clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean clientExists(String ip)
    {
        return clients.stream().anyMatch(clt->ip.equals(clt.getAddress()));
    }
    //serverul trebuie sa aiba o lista de clienti(ip si socket + threaduri personale)
    //pentru fiecare client avem nevoie de un thread de verificare pinguri si un thread de primire cereri.
    //de asemenea avem un thread care citeste pinguri de la toti userii si where ip matches apeleaza client.getCountdownThread().getPing();
}
