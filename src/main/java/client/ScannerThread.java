package client;

import common.kafka.ConsumerThread;
import common.kafka.KafkaTopic;
import common.kafka.ProducerThread;

import java.io.BufferedReader;
import java.io.IOException;

import static client.ClientApplication.getTopics;

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

                switch (state) {
                    case Unnamed:
                        System.out.println(resp);
                        if (resp.contains("saved successfully")) {
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
                        if(resp.contains("Could not create a topic")){
                            state=clientState.Idle;
                        }
                        break;
                    default:
                        System.out.println(resp);
                        break;
                }

                if(resp.contains("general thread"))
                {
                    String consumerName = "everyone";
                    String producerThreadName = "general";
                    String consumerThreadName = "general";
                    KafkaTopic topic = new KafkaTopic(consumerName,consumerThreadName,producerThreadName,false,this, resp.substring(resp.indexOf(":")+2));
                    getTopics().add(topic);
                }


                if(resp.contains("Successfully created topics"))
                {
                    String producerThreadName = resp.substring(resp.indexOf("\"")+1);
                    producerThreadName = producerThreadName.substring(0,producerThreadName.indexOf("\""));
//                    System.out.println(producerThreadName);


                    String consumerThreadName = resp.substring(0,resp.lastIndexOf("\""));
                    consumerThreadName = consumerThreadName.substring(consumerThreadName.lastIndexOf("\"")+1);
//                    System.out.println(consumerThreadName);


                    String consumerName = consumerThreadName.substring(0,consumerThreadName.indexOf("_"));
                    if(state!=clientState.WaitingForRoom)
                    {
                        System.out.println("You have joined a conversation with user " + consumerName);
                    }

                    KafkaTopic topic = new KafkaTopic(consumerName,consumerThreadName,producerThreadName,state==clientState.WaitingForRoom, this);

                    getTopics().add(topic);
                    topic.setActive(state==clientState.WaitingForRoom);
                    System.out.println("NEW TOPIC CREATED " + producerThreadName);

                    if(state==clientState.WaitingForRoom)
                        state=clientState.InConversation;



                }

            } catch (IOException e) {
                System.out.println("Could not read response from server. Make sure you do not have two instances of the client running.");
                e.printStackTrace();
            }
        }
    }
}
