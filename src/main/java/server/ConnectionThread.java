package server;

import common.models.ClientModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionThread extends Thread {
    private Socket clientSocket;
    private ClientModel client;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionThread(Socket socket, ClientModel client) {
        this.clientSocket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (clientSocket.isConnected()) {
                try {
                    String request = in.readLine();
                    if(request!=null) {
                        //TODO log all requests sent to server
                        if(client.getName()==null) {
                            if (ServerApplication.clients.stream().anyMatch(client -> request.equals(client.getName())))
                                out.println("Nickname is already in use.");
                            else {
                                client.setName(request);
                                out.println("Nickname saved successfully.");
                                System.out.println("Client with ip " + client.getAddress() + " set his nickname: " + request);
                            }
                            out.flush();
                        }
                        else
                        {
                            System.out.println(client.getName()+": "+request);
                            out.println("Request received but not yet treated");
                            out.flush();
                        }
                    }
                }catch(SocketException e) {
                    System.out.println("Exception for "+client.toString()+":\n"+e.getMessage());
                    client.die();
                    return;
                    /* Should be replaced with more elegant future solution. ex:
                    try {
                        sleep(2000);
                        continue();
                        //have a flag that checks if it failed 2 times in a row. Only then kill the client
                        //or maybe when the client actually reads in a separate thread, just send him a message to staaahp
                    } catch (InterruptedException ex) {
                        return;
                    }
                    */
                }

            }
            clientSocket.close();
            System.out.println("uhmmm messaging died");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("uhmmm messaging died2");

    }

}
