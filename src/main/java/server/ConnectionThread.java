package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private int port;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void run(int port) {
        try {
            while (clientSocket.getKeepAlive()) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine();
                if(request!=null)
                    System.out.println(request);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ConnectionThread(Socket socket) {
        this.clientSocket = socket;
    }
}
