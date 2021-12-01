package client;

import java.io.BufferedReader;
import java.io.IOException;

public class ScannerThread extends Thread{
    public enum clientState { Idle, Unnamed, WaitingForList, WaitingForRoom, InConversation }
    private final BufferedReader reader;

    private String lastRequest;
    private clientState state = clientState.Unnamed;

    public void setState(clientState state) {
        this.state = state;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public ScannerThread(BufferedReader reader) {
        this.reader = reader ;
    }

    public void run() {
        while(true) {
            try {
                String resp = reader.readLine();
//            System.out.println("****"+resp+"****");
                switch (state) {
                    case Unnamed:
                        System.out.println(resp);
                        if (resp.contains("success")) {
                            ClientApplication.name = lastRequest;
                            state = clientState.Idle;
                        }
                        break;
                    case WaitingForList:
                        System.out.println(resp);
                        state = clientState.Idle;
                        break;
                    default:
                        System.out.println(resp);
                        break;
                }

            } catch (IOException e) {
                System.out.println("Could not read response from server. Make sure you do not have two instances of the client running.");
                e.printStackTrace();
            }
        }
    }
}
