package common.kafka;

import client.ClientApplication;
import client.ScannerThread;

import java.util.ArrayList;
import java.util.List;

public class KafkaTopic {

    private String connectedUserName;
    private ConsumerThread ct;
    private ProducerThread pt;

    private int unreadMessageCount;

    private List<String> unreadMessages = new ArrayList<>();
    private boolean active = false;

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setActive(boolean active){this.active = active;}

    public boolean isActive(){return active;}

    public String getConnectedUserName() {
        return connectedUserName;
    }

    public void setConnectedUserName(String connectedUserName) {
        this.connectedUserName = connectedUserName;
    }

    public ConsumerThread getCt() {
        return ct;
    }

    public void setCt(ConsumerThread ct) {
        this.ct = ct;
    }

    public ProducerThread getPt() {
        return pt;
    }

    public void setPt(ProducerThread pt) {
        this.pt = pt;
    }

    public KafkaTopic(String connectedUserName, String consumerThreadName, String producerThreadName, boolean startNow, ScannerThread st) {
        this.connectedUserName = connectedUserName;
        this.ct = new ConsumerThread(consumerThreadName, this);
        this.ct.start();
        this.pt = new ProducerThread(producerThreadName,st);
        if(startNow)
            pt.start();
    }

    public void printUnreadMessages(){
        unreadMessages.forEach(msg -> System.out.println(connectedUserName + ": " + msg));
        unreadMessageCount=0;
    }

    public void addUnreadMessage(String message){
        unreadMessages.add(message);
        unreadMessageCount++;
        ClientApplication.getTopics().remove(this);
        ClientApplication.getTopics().add(0,this);
    }







}
