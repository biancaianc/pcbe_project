package server;

import common.models.ClientModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.net.SocketException;
import java.util.stream.Collectors;

public class ConnectionThread extends Thread {
    private Socket clientSocket;
    private ClientModel client;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionThread(Socket socket, ClientModel client) {
        this.clientSocket = socket;
        this.client = client;

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
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

                            if(request.equals("/list")) {
                                out.println(ServerApplication.clients.stream().map(clt -> clt.getName()).collect(Collectors.joining(",")));
                            }else {
                                if(request.startsWith("/msg ")) {
                                    Optional<ClientModel> userOptional = findUserByName(request.substring(5));
                                    if(userOptional.isPresent()) {
                                        ClientModel user = userOptional.get();
                                        // TOPIC NAME
                                        user.getMessagingThread().send(createTopicNames(user,client));

                                        System.out.println("s-a creat un topic intre "+client.getName()+" si "+user.getName());
                                        out.println(createTopicNames(client,user));
                                    }
                                    else {
                                        out.println("Could not create a topic for conversation!");
                                    }
                                }
                                else {
                                    out.println("Unknown command. Use /help to see available commands.");
                                }
                            }
                            out.flush();

                        }
                    }
                }catch(SocketException e) {
                    System.out.println("Exception for "+client.toString()+":\n"+e.getMessage());
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

    private String createTopicNames(ClientModel client, ClientModel user) {
        return "Successfully created topics p=\""+client.getName()+"_"+user.getName() + "\", c=\""+user.getName()+"_"+client.getName()+"\".";
    }

    private Optional<ClientModel> findUserByName(String substring) {
        return ServerApplication.clients.stream().filter(clt-> clt!=client && clt.getName().equals(substring)).findAny();
    }

    public void send(String message){
        out.println(message);
    }

}
