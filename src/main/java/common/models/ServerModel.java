package common.models;

import server.CountdownThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static common.ConnectionConstants.communicationPort;

public class ServerModel {
    private String address;
    private Socket connectionSocket;

    private PrintWriter writer;
    private BufferedReader reader;
    public ServerModel(String address) throws IOException {
        this.address = address;
        this.connectionSocket = new Socket(address, communicationPort);
        writer = new PrintWriter(connectionSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }
}
