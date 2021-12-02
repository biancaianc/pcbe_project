package client;

import common.kafka.ConsumerThread;
import common.kafka.ProducerThread;

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

    public clientState getCurrentState() {
        return state;
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
                    case WaitingForRoom:
                        System.out.println(resp);
//                        state = clientState.Idle;
                    default:
                        System.out.println(resp);
                        break;
                }


                if(resp.contains("Successfully created topics"))
                {
                    String producerThreadName = resp.substring(resp.indexOf("\"")+1);
                    producerThreadName = producerThreadName.substring(0,producerThreadName.indexOf("\""));
//                    System.out.println(producerThreadName);




                    String consumerThreadName = resp.substring(0,resp.lastIndexOf("\""));
                    consumerThreadName = consumerThreadName.substring(consumerThreadName.lastIndexOf("\"")+1);
//                    System.out.println(consumerThreadName);

                    new ConsumerThread(consumerThreadName).start();

                    state = clientState.InConversation;
                    ProducerThread pt = new ProducerThread(producerThreadName);


                }

            } catch (IOException e) {
                System.out.println("Could not read response from server. Make sure you do not have two instances of the client running.");
                e.printStackTrace();
            }
        }
    }
}
