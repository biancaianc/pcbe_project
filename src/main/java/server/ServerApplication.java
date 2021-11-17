package server;

import common.models.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static common.ConnectionConstants.communicationPort;

public class ServerApplication {

    private static List<Client> clients = new ArrayList<Client>();

    public static void main(String[] args) {
        while(true)
            //checkForConnections();//check if there is someone trying to connect into the app and creates the connection,creates the thread
        {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(communicationPort);
                Socket clientSocket = serverSocket.accept();
                clients.add(new Client(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //serverul trebuie sa aiba o lista de clienti(ip si socket + threaduri personale)
    //pentru fiecare client avem nevoie de un thread de verificare pinguri si un thread de primire cereri.
    //de asemenea avem un thread care citeste pinguri de la toti userii si where ip matches apeleaza client.getCountdownThread().getPing();
}
