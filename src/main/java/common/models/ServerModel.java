package common.models;

import java.io.IOException;
import java.net.Socket;

import static common.ConnectionConstants.communicationPort;

public class ServerModel {
    private String address;
    private Socket connectionSocket;

    public ServerModel(String address) throws IOException {
        this.address = address;
        this.connectionSocket = new Socket(address, communicationPort);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public Socket getConnectionSocket() {
//        return connectionSocket;
//    }
}
